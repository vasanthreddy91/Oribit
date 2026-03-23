package com.techtiera.docorbit.batch.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {
	private String canManageAccount;
	private String canSendEnvelope;
	private String apiAccountWideAccess;
	private String canSignEnvelope;
	private String adminOnly;
}
