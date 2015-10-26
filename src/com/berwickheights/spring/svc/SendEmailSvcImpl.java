/*
 * Copyright 2009 Berwick Heights Software, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is 
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations under the License.
 *  
 */
	
package com.berwickheights.spring.svc;


import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.util.Assert;


/**
 * @author stuart
 *
 */
public class SendEmailSvcImpl implements SendEmailSvc, InitializingBean {
	private JavaMailSender emailSender = null;
	private InternetAddress from = null;
	private String[] emailAddrOverride = null;
	private String[] emailAddrsCC = null;
	private String[] emailAddrsBCC = null;
	private int numWorkerThreads = 5;
	private ExecutorService threadPool;

	private final Log logger = LogFactory.getLog(this.getClass());


	/**
	 * Check that properties have been set correctly in bean definition
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(emailSender, "emailSender must be set");
		Assert.notNull(from, "from must be set");
		Assert.isTrue(numWorkerThreads > 0, "numWorkerThreads must be greater than 0");

		// Set up thread pool to send email messages asynchronously
		threadPool = Executors.newFixedThreadPool(numWorkerThreads);
	}

	public void setEmailAddrOverride(String emailAddrOverride) {
		this.emailAddrOverride = emailAddrOverride.split(",");
		if (emailAddrOverride != null) {
			logger.warn("Warning: Email address override in use!!! All emails will be sent to: "
					+ emailAddrOverride);
		}
	}

	public void setEmailAddrsCC(String emailAddrsCC) {
		this.emailAddrsCC = getEmailAddrsFromConfig(emailAddrsCC, "Email copies"); 
	}

	public void setEmailAddrsBCC(String emailAddrsBCC) {
		this.emailAddrsBCC = getEmailAddrsFromConfig(emailAddrsBCC, "Blind email copies"); 
	}

	private String[] getEmailAddrsFromConfig(String emailAddrs, String emailType) {
		String[] result = emailAddrs.split(",");
		if (result != null) {
			String logMsg = "Warning: " + emailType + " will be sent to: ";
			for (int idx = 0; idx < result.length; idx++) {
				if (idx > 0) {
					logMsg  += ", ";
				}
				logMsg += result[idx];
			}
			logger.warn(logMsg);
		}
		
		return result;
	}


	/**
	 * Called when the bean is destroyed (i.e., application is shutting down)
	 */
	public void destroy() {
		// Shut down the thread pool
		threadPool.shutdownNow();
	}

	public void sendEmail(String emailAddr, String subject, String msgText,
			String attachmentFileName, File attachment) {
		sendEmail(emailAddr, subject, msgText, attachmentFileName, attachment, true);
	}

	public void sendEmail(String emailAddr, String subject, String msgText,
			String attachmentFileName, File attachment, boolean runInBackground) {
		sendEmail(new String[] { emailAddr }, subject, msgText, attachmentFileName, attachment,
				runInBackground);
	}

	public void sendEmail(String[] emailAddrs, String subject, String msgText,
			String attachmentFileName, File attachment) {
		sendEmail(emailAddrs, subject, msgText, attachmentFileName, attachment, true);
	}

	public void sendEmail(String[] emailAddrs, String subject, String msgText,
			String attachmentFileName, File attachment, boolean runInBackground) {
		if (emailAddrOverride != null) {
			logger.warn("Warning: Email address override in use!!! Email will be sent to: "
					+ emailAddrOverride[0]);
			emailAddrs = this.emailAddrOverride;
		}

		WorkerThread worker = new WorkerThread(emailAddrs, subject, msgText,
				attachmentFileName, attachment);

		if (runInBackground) {
			//
			// Send email message on a separate thread so that UI is not blocked while SMTP service
			// processes the request
			//
			threadPool.execute(worker);
		}
		else {
			worker.run();
		}
	}


	/**
	 * Set the mail sender used to send email message.
	 */
	public void setEmailSender(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}


	/**
	 * Sets the "from" part of the email
	 */
	public void setFrom(String from) {
		try {
			this.from = new InternetAddress(from);
		}
		catch (AddressException e) {
			throw new IllegalArgumentException("The given From address (" +
				 from + " is not valid");
		}
	}


	/**
	 * Sets the number of asynchronous threads in thread pool used to service sending of
	 * email messages
	 */
	public void setNumWorkerThreads(int numWorkerThreads) {
		this.numWorkerThreads = numWorkerThreads;
	}



	//
	// Asynchronous thread class that processes sending of email messages so that UI is not
	// blocked while connecting to SMTP server
	//
	private class WorkerThread implements Runnable {
		private String[] emailAddrs;
		private String subject;
		private String msgText;
		private File attachment;
		private String attachmentFileName;


		public WorkerThread(String[] emailAddrs, String subject, String msgText,
				String attachmentFileName, File attachment) {
			this.emailAddrs = emailAddrs;
			this.subject = subject;
			this.msgText = msgText;
			this.attachmentFileName = attachmentFileName;
			this.attachment = attachment;
		}



		public void run() {
			try {
				if (logger.isDebugEnabled()) {
					String logMsg = "Sending email message to ";
					for (int idx = 0; idx < emailAddrs.length; idx++) {
						if (idx > 0) {
							logMsg  += ", ";
						}
						logMsg += emailAddrs[idx];
					}
					logger.debug(logMsg);
				}

				MimeMessagePreparator preparator = new MimeMessagePreparator() {
					public void prepare(MimeMessage msg) throws Exception {
						MimeMessageHelper helper = new MimeMessageHelper(msg, true);

						// Set From, To and Subject
						helper.setFrom(from);
						helper.setTo(emailAddrs);
						if (emailAddrsCC != null) {
							helper.setCc(emailAddrsCC);
						}
						if (emailAddrsBCC != null) {
							helper.setBcc(emailAddrsBCC);
						}
						helper.setSubject(subject);

						// Set up HTML-based body
						helper.setText(msgText, true);

						// Add attachment if available
						if (attachment != null) {
							helper.addAttachment(attachmentFileName, attachment);
						}
					}
				};


				try {
					emailSender.send(preparator);
				}
				catch (MailException e) {
					logger.error(genErrorMsg(), e);
				}
			}
			catch (Throwable t) {
				logger.error("Caught exception in send email worker thread: "
						+ genErrorMsg(), t);
			}
		}



		private String genErrorMsg() {
			String msg = "Could not send email to ";
			for (int idx = 0; idx < emailAddrs.length; idx++) {
				if (idx > 0) {
					msg  += ", ";
				}
				msg += emailAddrs[idx];
			}
			msg += ", subject: " + subject;

			return msg;
		}
	}
}
