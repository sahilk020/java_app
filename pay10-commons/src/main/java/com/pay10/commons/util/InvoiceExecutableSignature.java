package com.pay10.commons.util;


public enum InvoiceExecutableSignature {
	CSV5 ("CSV5" , (byte) 0x00 , (byte) 0x05 , new byte[] { (byte) 0xfe, (byte) 0xff, (byte) 0x00, (byte) 0x00} ),
	 CSV1 ("CSV1" , (byte) 0x00 , (byte) 0x05 , new byte[] { (byte) 0xfe, (byte) 0xff, (byte) 0x00, (byte) 0x00} ),
	 CSV2 ("CSV2" , (byte) 0x00 , (byte) 0x05 , new byte[] { (byte) 0xff, (byte) 0xfe, (byte) 0x00, (byte) 0x00}),
	CSV3 ("CSV3" , (byte) 0x00 , (byte) 0x05 , new byte[] { (byte) 0xef, (byte) 0xbb, (byte) 0xbf, (byte) 0x00}),
	CSV4 ("CSV4" , (byte) 0x00 , (byte) 0x05 , new byte[] { (byte) 0x74, (byte) 0x65, (byte) 0x78, (byte) 0x74, (byte) 0x2F, (byte) 0x70, (byte) 0x6C, (byte) 0x61,
			(byte) 0x69, (byte) 0x6E, (byte) 0x3B, (byte) 0x20, (byte) 0x63, (byte) 0x68, (byte) 0x61, (byte) 0x72, (byte) 0x73, (byte) 0x65,
			(byte) 0x74, (byte) 0x3D, (byte) 0x55, (byte) 0x54, (byte) 0x46,
			(byte) 0x2D, (byte) 0x38}),
	CSV6 ("CSV6" , (byte) 0x00 , (byte) 0x05 , new byte[] { (byte) 0x31, (byte) 0x31, (byte) 0x37, (byte) 0x20, (byte) 0x31, (byte) 0x31, (byte) 0x35, (byte) 0x20,
			(byte) 0x30, (byte) 0x34, (byte) 0x35, (byte) 0x20, (byte) 0x30, (byte) 0x39, (byte) 0x37, (byte) 0x20, (byte) 0x31, (byte) 0x35,
			(byte) 0x20, (byte) 0x30, (byte) 0x39, (byte) 0x20, (byte) 0x31,
			(byte) 0x30, (byte) 0x35, (byte) 0x20, (byte) 0x31, (byte) 0x30, (byte) 0x35})
	
	;
	// PNG ("PNG"	, (byte) 0x00, (byte) 0x05, new byte[] { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A});
	
	 
	

	/* Here more enumeration */
	private String description;
	private byte offset;
	private byte size;
	private byte[] magicNumber;

	private InvoiceExecutableSignature(String description, byte offset, byte size, byte[] magicNumber) {

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
