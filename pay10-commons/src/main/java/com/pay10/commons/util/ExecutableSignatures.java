package com.pay10.commons.util;

public enum ExecutableSignatures {
	/*
	WINDOWS_EXE("Windows|DOS executable file", (byte) 0x00, (byte) 0x02, new byte[] { (byte) 0x4d, (byte) 0x5a }),
	JAVA_BYTECODE("Java Bytecode", (byte) 0x00, (byte) 0x04,
			new byte[] { (byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe }),
	JPEG : ff d8 ff e0
	PNG :  89 50 4E 47 0D 0A 1A 0A
	*/
	PDF		("%PDF-", (byte) 0x00, (byte) 0x05, new byte[] { (byte) 0x25, (byte) 0x50, (byte) 0x44, (byte) 0x46, (byte) 0x2D}),
	PNG		("PNG"	, (byte) 0x00, (byte) 0x05, new byte[] { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A}),
	JPEG	("JPEG"	, (byte) 0x00, (byte) 0x05, new byte[] { (byte) 0xff, (byte) 0xd8, (byte) 0xff, (byte) 0xe0}),
	JPG 	("JPG"	, (byte) 0x00, (byte) 0x05, new byte[] { (byte) 0xff, (byte) 0xd8})
	
	;

	/* Here more enumeration */
	private String description;
	private byte offset;
	private byte size;
	private byte[] magicNumber;

	private ExecutableSignatures(String description, byte offset, byte size, byte[] magicNumber) {

		this.description = description;
		this.offset = offset;
		this.size = size;
		this.magicNumber = magicNumber;

	}

	public String getDescription() {
		return this.description;
	}

	public byte getOffset() {
		return this.offset;
	}

	public byte getSize() {
		return this.size;
	}

	public byte[] getMagicNumbers() {
		return this.magicNumber;
	}
}
