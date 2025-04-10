package com.arizona.lipit.domain.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FcmMessage {

	private boolean validateOnly;
	private Message message;

	public FcmMessage() {
	}

	public FcmMessage(boolean validateOnly, Message message) {
		super();
		this.validateOnly = validateOnly;
		this.message = message;
	}

	/** Message
	 *
	 * @author USER1
	 *
	 */
	@Setter
	@Getter
	public static class Message {
		private Notification notification;
		private String token;

		public Message() {
		}

		public Message(Notification notification, String token) {
			super();
			this.notification = notification;
			this.token = token;
		}
	}

	/** Notification
	 *
	 * @author USER1
	 *
	 */
	@Setter
	@Getter
	public static class Notification {
		private String title;
		private String body;
		private String image;

		public Notification() {
		}

		public Notification(String title, String body, String image) {
			super();
			this.title = title;
			this.body = body;
			this.image = image;
		}
	}
}
