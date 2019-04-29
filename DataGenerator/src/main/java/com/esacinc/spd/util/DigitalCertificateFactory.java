package com.esacinc.spd.util;

import java.util.Calendar;
import java.util.Date;

import org.hl7.fhir.r4.model.CodeableConcept;

import com.esacinc.spd.model.VhDirDigitalCertificate;

public class DigitalCertificateFactory {

	static private String[] certs = null;
	
	public DigitalCertificateFactory() { init();}

	static private void init() {
	// 5 sample certificates to use for samples
	certs = new String[5];
	certs[0] = "-----BEGIN CERTIFICATE-----" +
		"MIIE2DCCAsACCQDMxyOS8py9rTANBgkqhkiG9w0BAQsFADAuMQswCQYDVQQGEwJV" +
		"UzEOMAwGA1UECAwFVGV4YXMxDzANBgNVBAcMBkRhbGxhczAeFw0xOTA0MTUxOTIy" +
		"MjFaFw0yMDA0MTQxOTIyMjFaMC4xCzAJBgNVBAYTAlVTMQ4wDAYDVQQIDAVUZXhh" +
		"czEPMA0GA1UEBwwGRGFsbGFzMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKC" +
		"AgEAzUVfF7+Pif89oqJsIB9n07iRIAD0Fz5fZRsxucwm0DXk4VBXDXOso4JAM960" +
		"gR75ExABQuIIWmGNwdp74aMdMLD3kvvd0DVBvjb9i9fsLDpP8N7RFMMNyKCKFdmo" +
		"PU+qEO+nM/LHp4hiYzeMoOQevtRBVo6URfQnoCek1j1+HiYaxeV9LrErgsI0i2P7" +
		"4sCprwE8iHqbauKNFJahBite90kVL4cRc0r9HZWzX0j10Xzw7kSD1dEHG7NxDgqy" +
		"usr3aVFadTVIl4wl2KHeZdGOQiF0kfzb4qdIhVyNABYYP6Rfc+aGZK4EysQyXW7R" +
		"2avgTxHe1EZlUqNkmDeuugQxljsjWb5yevfyNTCrSy1eYpFlzzetrEwnLXp74IMA" +
		"yvFaR6yQNKuQXE47XJVESDpoQ+PqowQnnFqTtCp+NzFbp2nwsZD6wJMsUviM0u9t" +
		"haytrcTniy86mm5PL/0mJVasgRF5U5r3PzIvMDlleiqyQvak6+z4txEA29hlsbBg" +
		"mBArQ+3XI9bXnP8qbMs1AuVbMvj+zWpBrYj3gRhpMOubKT177wTy7trCT8/51bvL" +
		"mybDsoQsJxX1xCzMK6518qQY+4Pu8kEzdpSJVMAZl/9Qzq6dYJy2iAgcMBf1zo1H" +
		"RGdhQJ6iKfaSvBDM1dqPhoFUlgX2Ccxq+hFkBI5+6RLKZn0CAwEAATANBgkqhkiG" +
		"9w0BAQsFAAOCAgEAADxVF4DC5Lr1PpkaMRILRkydW3AEB2vSkNkRbvYtVAMIli2B" +
		"q1RacCm5e7IfQCF4HGJzTjwGHCAyxl2+suEyQ1jjT9biO3kWHdHmTKEoxeDsaSUB" +
		"w+jziee9PhVUayOqRh+zn8WDBPDwm8EoE9lQfK/TAVlXxbom0YeGVJKMN6xr/EBI" +
		"dY1/gpbJ9/QQIWe+JeG9MxDHP8WEmVqv9RwBI4CtLmhxqXxyKG8Lmps3rdgiQ9hL" +
		"DHqGfu1F4St3sjnjTUZdB67G+HN3TKoN2InBcjGxIsHW6kyMamo1kFc5hgOG9CnU" +
		"4fqn4HhXoWcmYDq3aGet4+SAkQ03D3/RyeviL2f9ptwX4Bm7uZcdI/ZkqQdLIYZX" +
		"pSOUQsidq6NWcZffKfBNgoiPWj1TynNdKfM09sxfTk94OB1k9UaP7KlIR8VuZsoS" +
		"2vC0HYf8x+16Wll3vnkumYVDoOpwOxnSv1q/TcksPmwJftEonZdqWA1HiuytXqcn" +
		"Il5jeqcLchyn9MWlZzl4BrqOZVBMrFjzOmSTPHJDdKngrP7ZjQKiVUHjFeMpfqmN" +
		"h4qfhg9D3bnIrVtMf0mvHiy/gZaVwZ0xTPmCRgK3k71ZAhksxvPA/e4cEepoV+n8" +
		"DQrcPNSd5ml8tq1SnB/bx/rXm6IF8yvuHlEHqV6wCrC33DLsHMv1VT0nzbc=" +
		"-----END CERTIFICATE-----";
	
	certs[1] = "-----BEGIN CERTIFICATE-----" +
		"MIIE2DCCAsACCQDjhQnQs4wMXDANBgkqhkiG9w0BAQsFADAuMQswCQYDVQQGEwJV" +
		"UzEOMAwGA1UECAwFVGV4YXMxDzANBgNVBAcMBlNhY2hzZTAeFw0xOTA0MTUxOTM1" +
		"MzRaFw0yMDA0MTQxOTM1MzRaMC4xCzAJBgNVBAYTAlVTMQ4wDAYDVQQIDAVUZXhh" +
		"czEPMA0GA1UEBwwGU2FjaHNlMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKC" +
		"AgEA4e0UOJlkQnGVtndeLFIOjQGQvOrOZl9Myt4B8KfmNHq81mydtptWvioIcPOq" +
		"jHlWVtmylV7kFS5EiTkvxmjT7uGJQdfaCunX9CooQGcF0U6XNSvyLRcStMN+Cn6d" +
		"sC3QW2rUuIZV6qQrP/hdW5f5IqZA6UaL8rPV2xKhl33o+GOX/FuRDXW/V9ytqtc1" +
		"Sm3VSV/SWOhGzHmtY23Cg7NlJ7+j9FmQbaFKfXmIgY5Qzzx61NcdGCr9i3IFpHWY" +
		"4u7n1lM1m/Vufc165JDBUtHZ7DRN4RHbTtxmX2SNrCjDgD/Ww3UBCdVDkcxt9FmG" +
		"qxOCi11C1pJGjIOX+znafmFjpmzUIkGma5g27a97ZcyVZ+hVQBYfce2T6nY1I/yj" +
		"lp/gkVHtQVmNaYjSVb8r9zeyIRAgOGaKt5d4iefjSPQ6BLRfrPveFQxscxLjOEk7" +
		"+yTq2XJFDycP5wMEOixYobAPfhFLb0PxAyZ34VMw5yxHxFS4FE4aDh1ASG+3D4ay" +
		"CG/eJ6qc5lNa0F5GOrI68BBTob6jTmM7KFp2cgYrGMy+QGLyV68dKfxx/4KWi3Kx" +
		"w/PNeOgTyneJWmxqAARoHQRRC9LaFz1Aj402Y8FKcqSlrTxJ7RJhmllGP1S8R78N" +
		"zY0X0zF8xubL4tiIgZ5XPLqp217jivRuHoBLcv8eEzD/hAMCAwEAATANBgkqhkiG" +
		"9w0BAQsFAAOCAgEAgmxQ1amNXH6LpM2D6Edh2CgcL+tRrtS0GBXnnDcwZyoPOTIe" +
		"tN4bRIyYZVYf1fd3JlgPHbCleUuxAoIzB1bK5968YYQpVIf7zmMvPcV+zFZQJP3c" +
		"+KSSksuQiHm24O8/GYEmUI9/oex1YcElog9ef+m17FgUYfD0TebwUA0zrLgbMqFh" +
		"8fkTesiyAdT+TOa+2W84LddwR7xC3SAxrDeiUaNSKK+Rqd/NONmLwJCDJjZEeBE+" +
		"MkW5s040YXru9cZFVZ/U9V/s+d+3v3YfkzARGdU/fAjoBq0iAqcmdz0FvsUwBTno" +
		"gn+ZrybJ+TCnYxAbNjgnW23UyQ+4t3VUr0QaA47BUfD5LGknPjsPHB9aA+1sI3t+" +
		"1gHuETA7OcJPh1wd6BYOPoV2VXcgA7AVVcMdviE/A2gakWhZzcF8Ep3WbSCVSls6" +
		"fsh4lbRo4wczc9hUaryFo0eBVaw44V7qtwIJw2IUcWA7U6w8q62gL+tOJhp2AU5K" +
		"fKS5yP5HWBhyQV2IHtfoO1YMHXkhhDaMI3giMt12HyV+U+aJ0KODvcHDE8Qp6BR2" +
		"AbbkMirbBeDCjdC30NF6lD7hPJAfpzc5C1vE3IS98BnqjaxNJSthXFgZWggrtyYi" +
		"5hn6ExT1XblkKvgox7zgq9mG430V24CT5uVzqJSc2S+U7R0ntCYuydgaP58=" +
		"-----END CERTIFICATE-----";
	certs[2] = "-----BEGIN CERTIFICATE-----" +
		"MIIE5DCCAswCCQCHIWZ/N40MkzANBgkqhkiG9w0BAQsFADA0MQswCQYDVQQGEwJV" +
		"UzERMA8GA1UECAwIQ29sb3JhZG8xEjAQBgNVBAcMCUV2ZXJncmVlbjAeFw0xOTA0" +
		"MTUxOTM5MzdaFw0yMDA0MTQxOTM5MzdaMDQxCzAJBgNVBAYTAlVTMREwDwYDVQQI" +
		"DAhDb2xvcmFkbzESMBAGA1UEBwwJRXZlcmdyZWVuMIICIjANBgkqhkiG9w0BAQEF" +
		"AAOCAg8AMIICCgKCAgEAq30Nk0+N4bUJ8J7HVpWXqG9Dov1Sj1Um+t5uYCMEZ5XZ" +
		"L3+sl9TeGhTwtUQTn5HgtSsQWdvMDffwLfsoOukZNNYsBRQ+10pNhWTaOSN5xibA" +
		"9p5hEeyS/9n/xlD+AumLu3aSeQYTILEXSGbuidh9iYiIPgRI5Eud1vwypDRo3GA3" +
		"ZC2uG1IAQUjoi3QI5DINt2IO3rH1fvS+6MJGL9jMuXbLaho0Sqztg+gGHP2uFqon" +
		"stzJUzr/IWm+Z1i+jGmxdGG19vlMS340UsPxYV5xuo31NTC6VtsjoSflIErp4LmQ" +
		"rg4X93c95rp2XuylInaRhXIhaXJjsd4gG+ukAgXMilbeSiSpW/l8uVSWNLuYNErm" +
		"zeNabirn1IaIqWYx0B+J7Nsl5u6mZD/Di/Feu0rWr9pfaIHXQWnBDFR0IBrQhEj8" +
		"9+gwncHD9dOmuhJDLjxFjNAIUR2Bc2Ph+jlQOasaPHMDg3mGtxMu12z1++dX8BkY" +
		"eQFe6tUW8g78VA4c5fqbVTmkB7/VK1bpSNYT2gkaITwqIQwl3iCHFG8tbgcU8lN/" +
		"E0V3+yiIMExETESPhxBX0cCoYxRje1vSrxyznj7EQ7iJ4026ci0doopwsJmQAkzr" +
		"rYjmhowZS3fpzjr1kz8Ghrdre7ro17NUJYWu3tYjZkBkY9Z1TxSWLw0UQjguHp8C" +
		"AwEAATANBgkqhkiG9w0BAQsFAAOCAgEAKN5U+Rm1eL2b7idzn89gvwQLJqDqRsQo" +
		"YJw2D+kFwhEjw5IFwd/67WaI2HOMq5Mqu5YT9bkRGkA24sh1IkH50HvJ4idZgbB9" +
		"MbcpiMvrP3yvWTvVwHvzdLkOqn0M0j5RL0/pPuCsTFZ93SFn1PKcnJeyOTRQrb1R" +
		"DsHS1di/AclQnnv65ed7qqNdKGFp5tVAQiHBK8sT3oIufZMTWCD47FpAVxw0jCYD" +
		"lUrwWZxqqSOx/No3R7BgTdfQLaU107UeoGe6cgiwZ1L1+oJPKuHsER+ydotrbew9" +
		"DrvlL0c70/vP5YFqlZxLyoguMyOypliqhqCBR+D/jRuexF3KfY0bErxNDUKicrwU" +
		"m+S7YQ0Z9v29IglrC6zkKkVGK+z6NFq1ND7opt7vxrDc97QhFMRELQpx+vvJYuac" +
		"Gh2oc2aXC/e70IztUc+eukx+trK3deqlzg11anJj0Yehu/I6zOK5s2JSSNfcO00O" +
		"T0vtWAQGxFsnG1FiRaRP9VYwkh+LdYns5hOLJsUKhMmVNbHVvajAxU5YAGghdxi2" +
		"wSMIwlHK1MbsKwPzXOtonta0Bn0cAkSyfmlpXrZs1PkrxwyyNPYisV0zzlvtDQV0" +
		"QGIPs0N9k18ywBu2onwnGA7nvg8a/zIeE7OVgC/iEVDDN3kWZVVTZeHQxKvz0ii+" +
		"e1smllz52UE=" +
		"-----END CERTIFICATE-----";
	}
	
	static public String getNthCert(int n) {
		if (certs == null) {
			init();
		}
		if (n >= 0 && n < certs.length) {
			return certs[n];
		}
		return null;
	}
	
	static public VhDirDigitalCertificate makeDigitalCertificate(int nth, String type, String use, String trustFramework, String standard, Date expiration) {
		VhDirDigitalCertificate cert = new VhDirDigitalCertificate();
		cert.setType(ResourceFactory.makeCoding(type,  type, "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate", false));
		cert.setUse(ResourceFactory.makeCoding(use,  use, "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate", false));
		cert.setCertificateStandard(ResourceFactory.makeCoding(standard,  standard, "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate", false));
		cert.setExpirationDate(expiration);
		CodeableConcept certTrust = new CodeableConcept();
		certTrust.addCoding(ResourceFactory.makeCoding(trustFramework,  trustFramework, "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate", false));
		cert.setTrustFramework(certTrust);

		return cert;
	}

}
