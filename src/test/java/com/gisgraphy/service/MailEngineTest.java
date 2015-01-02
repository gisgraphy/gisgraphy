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

import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;

/**
 * @author Bryan Noll
 */
public class MailEngineTest extends AbstractTransactionalTestCase {
    MailEngine mailEngine;

    SimpleMailMessage mailMessage;

    JavaMailSenderImpl mailSender;


    @Test
    public void testSend() throws Exception {
	// mock smtp server
	Wiser wiser = new Wiser();
	// set the port to a random value so there's no conflicts between tests
	int port = 2525 + (int) (Math.random() * 100);
	mailSender.setPort(port);
	wiser.setPort(port);
	wiser.start();

	Date dte = new Date();
	this.mailMessage.setTo("foo@bar.com");
	String emailSubject = "grepster testSend: " + dte;
	String emailBody = "Body of the grepster testSend message sent at: "
		+ dte;
	this.mailMessage.setSubject(emailSubject);
	this.mailMessage.setText(emailBody);
	this.mailEngine.send(this.mailMessage);

	wiser.stop();
	assertTrue(wiser.getMessages().size() == 1);
	WiserMessage wm = wiser.getMessages().get(0);
	assertEquals(emailSubject, wm.getMimeMessage().getSubject());
	assertEquals(emailBody, wm.getMimeMessage().getContent());
    }

    @Test
    public void testSendMessageWithAttachment() throws Exception {
	final String ATTACHMENT_NAME = "boring-attachment.txt";

	// mock smtp server
	Wiser wiser = new Wiser();
	int port = 2525 + (int) (Math.random() * 100);
	mailSender.setPort(port);
	wiser.setPort(port);
	wiser.start();

	Date dte = new Date();
	String emailSubject = "grepster testSendMessageWithAttachment: " + dte;
	String emailBody = "Body of the grepster testSendMessageWithAttachment message sent at: "
		+ dte;

	ClassPathResource cpResource = new ClassPathResource(
		"/test-attachment.txt");
	mailEngine.sendMessage(new String[] { "foo@bar.com" }, mailMessage
		.getFrom(), cpResource, emailBody, emailSubject,
		ATTACHMENT_NAME);

	wiser.stop();
	assertTrue(wiser.getMessages().size() == 1);
	WiserMessage wm = wiser.getMessages().get(0);
	MimeMessage mm = wm.getMimeMessage();

	Object o = wm.getMimeMessage().getContent();
	assertTrue(o instanceof MimeMultipart);
	MimeMultipart multi = (MimeMultipart) o;
	int numOfParts = multi.getCount();

	boolean hasTheAttachment = false;
	for (int i = 0; i < numOfParts; i++) {
	    BodyPart bp = multi.getBodyPart(i);
	    String disp = bp.getDisposition();
	    if (disp == null) { // the body of the email
		Object innerContent = bp.getContent();
		MimeMultipart innerMulti = (MimeMultipart) innerContent;
		assertEquals(emailBody, innerMulti.getBodyPart(0).getContent());
	    } else if (disp.equals(Part.ATTACHMENT)) { // the attachment to the
		// email
		hasTheAttachment = true;
		assertEquals(ATTACHMENT_NAME, bp.getFileName());
	    } else {
		fail("Did not expect to be able to get here.");
	    }
	}
	assertTrue(hasTheAttachment);
	assertEquals(emailSubject, mm.getSubject());
    }
    
    public void setMailSender(JavaMailSenderImpl mailSender) {
	this.mailSender = mailSender;
    }

    public void setMailEngine(MailEngine mailEngine) {
	this.mailEngine = mailEngine;
    }
    
    public void setMailMessage(SimpleMailMessage mailMessage) {
	this.mailMessage = mailMessage;
    }
    
}
