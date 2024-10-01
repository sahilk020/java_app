package com.crmws.loadtest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EmptyStackException;
import java.util.Stack;

public class MessageDigestProvider {
	private static Stack<MessageDigest> stack = new Stack<MessageDigest>();

	public static MessageDigest provide() throws Exception {
		MessageDigest digest = null;
		try {
			digest = stack.pop();
		} catch (EmptyStackException emptyStackException) {
			try {
				digest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
				throw new Exception("Hashing algoritham not found");
			}
		}
		return digest;
	}

	public static void consume(MessageDigest digest) {
		stack.push(digest);
	}
}
