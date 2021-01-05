package org.prodapt.raf.dto;

import java.util.List;
import java.util.Map;

public class Mail {

    private String from;
    private String[] mailTo;
    private String[] mailCc;
    private String[] mailBcc;
    private String subject;
    private List<Object> attachments;
    private Map<String, Object> props;

    public Mail() {}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String[] getMailTo() {
		return mailTo;
	}

	public void setMailTo(String[] strings) {
		this.mailTo = strings;
	}
	
	public String[] getMailCc() {
		return mailCc;
	}

	public void setMailCc(String[] strings) {
		this.mailCc = strings;
	}
	
	public String[] getMailBcc() {
		return mailBcc;
	}

	public void setMailBcc(String[] strings) {
		this.mailBcc = strings;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<Object> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Object> attachments) {
		this.attachments = attachments;
	}

	public Map<String, Object> getProps() {
		return props;
	}

	public void setProps(Map<String, Object> props) {
		this.props = props;
	}

}
