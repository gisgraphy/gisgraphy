/*******************************************************************************
 *   Gisgraphy Project 
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 * 
 *  Copyright 2008  Gisgraphy project 
 *  David Masclet <davidmasclet@gisgraphy.com>
 *  
 *  
 *******************************************************************************/
package com.gisgraphy.service;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * Class for sending e-mail messages based on Velocity templates or with
 * attachments.
 * 
 * @author Matt Raible
 */
public class MailEngine {
    private final Log log = LogFactory.getLog(MailEngine.class);

    private MailSender mailSender;

    private VelocityEngine velocityEngine;

    public void setMailSender(MailSender mailSender) {
	this.mailSender = mailSender;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
	this.velocityEngine = velocityEngine;
    }

    /**
     * Send a simple message based on a Velocity template.
     * 
     * @param msg
     *                the message to populate
     * @param templateName
     *                the Velocity template to use (relative to classpath)
     * @param model
     *                a map containing key/value pairs
     */
    @SuppressWarnings("unchecked")
    public void sendMessage(SimpleMailMessage msg, String templateName,
	    Map model) {
	String result = null;

	try {
	    result = VelocityEngineUtils.mergeTemplateIntoString(
		    velocityEngine, templateName, model);
	} catch (VelocityException e) {
	    log.error(e.getMessage());
	}

	msg.setText(result);
	send(msg);
    }

    /**
     * Send a simple message with pre-populated values.
     * 
     * @param msg
     *                the message to send
     */
    public void send(SimpleMailMessage msg) {
	try {
	    mailSender.send(msg);
	} catch (MailException ex) {
	    // log it and go on
	    log.error(ex.getMessage());
	}
    }

    /**
     * Convenience method for sending messages with attachments.
     * 
     * @param recipients
     *                array of e-mail addresses
     * @param sender
     *                e-mail address of sender
     * @param resource
     *                attachment from classpath
     * @param bodyText
     *                text in e-mail
     * @param subject
     *                subject of e-mail
     * @param attachmentName
     *                name for attachment
     * @throws MessagingException
     *                 thrown when can't communicate with SMTP server
     */
    public void sendMessage(String[] recipients, String sender,
	    ClassPathResource resource, String bodyText, String subject,
	    String attachmentName) throws MessagingException {
	MimeMessage message = ((JavaMailSenderImpl) mailSender)
		.createMimeMessage();

	// use the true flag to indicate you need a multipart message
	MimeMessageHelper helper = new MimeMessageHelper(message, true);

	helper.setTo(recipients);
	helper.setFrom(sender);
	helper.setText(bodyText);
	helper.setSubject(subject);

	helper.addAttachment(attachmentName, resource);

	((JavaMailSenderImpl) mailSender).send(message);
    }
}
