package eterea.migration.api.rest.configuration;

import eterea.migration.api.rest.extern.*;
import eterea.migration.api.rest.model.*;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.stream.Stream;

@Configuration
@ImportRuntimeHints(NativeRuntimeHints.Hints.class)
public class NativeRuntimeHints {

    static class Hints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Register DTOs and Models for reflection
            hints.reflection().registerType(Auditable.class, MemberCategory.values());
            hints.reflection().registerType(InformacionPagador.class, MemberCategory.values());
            hints.reflection().registerType(OrderNote.class, MemberCategory.values());
            hints.reflection().registerType(Payment.class, MemberCategory.values());
            hints.reflection().registerType(Product.class, MemberCategory.values());
            hints.reflection().registerType(ProductTransaction.class, MemberCategory.values());
            hints.reflection().registerType(InformacionPagadorWeb.class, MemberCategory.values());
            hints.reflection().registerType(OrderNoteWeb.class, MemberCategory.values());
            hints.reflection().registerType(PaymentWeb.class, MemberCategory.values());
            hints.reflection().registerType(ProductTransactionWeb.class, MemberCategory.values());
            hints.reflection().registerType(ProductWeb.class, MemberCategory.values());

            // JSch hints for SFTP, registered by name to avoid compile-time errors
            Stream.of(
                // Base classes
                "com.jcraft.jsch.UserAuthNone",
                "com.jcraft.jsch.UserAuthPassword",
                "com.jcraft.jsch.UserAuthPublicKey",
                "com.jcraft.jsch.UserAuthGSSAPIWithMIC",
                "com.jcraft.jsch.Session",
                "com.jcraft.jsch.ChannelSftp",
                "com.jcraft.jsch.DHG1",
                "com.jcraft.jsch.DHG14",
                "com.jcraft.jsch.DHGEX",
                "com.jcraft.jsch.DHGEX256",
                // JCE implementation classes
                "com.jcraft.jsch.jce.Random",
                "com.jcraft.jsch.jce.AES128CBC",
                "com.jcraft.jsch.jce.AES128CTR",
                "com.jcraft.jsch.jce.AES192CBC",
                "com.jcraft.jsch.jce.AES192CTR",
                "com.jcraft.jsch.jce.AES256CBC",
                "com.jcraft.jsch.jce.AES256CTR",
                "com.jcraft.jsch.jce.ARCFOUR",
                "com.jcraft.jsch.jce.ARCFOUR128",
                "com.jcraft.jsch.jce.ARCFOUR256",
                "com.jcraft.jsch.jce.BlowfishCBC",
                "com.jcraft.jsch.jce.DH",
                "com.jcraft.jsch.jce.DSA",
                "com.jcraft.jsch.jce.ECDH256",
                "com.jcraft.jsch.jce.ECDH384",
                "com.jcraft.jsch.jce.ECDH521",
                "com.jcraft.jsch.jce.HMACMD5",
                "com.jcraft.jsch.jce.HMACMD596",
                "com.jcraft.jsch.jce.HMACSHA1",
                "com.jcraft.jsch.jce.HMACSHA196",
                "com.jcraft.jsch.jce.HMACSHA256",
                "com.jcraft.jsch.jce.KeyPairGenDSA",
                "com.jcraft.jsch.jce.KeyPairGenECDSA",
                "com.jcraft.jsch.jce.KeyPairGenRSA",
                "com.jcraft.jsch.jce.MD5",
                "com.jcraft.jsch.jce.PBKDF",
                "com.jcraft.jsch.jce.RSA",
                "com.jcraft.jsch.jce.SHA1",
                "com.jcraft.jsch.jce.SHA256",
                "com.jcraft.jsch.jce.SHA384",
                "com.jcraft.jsch.jce.SHA512",
                "com.jcraft.jsch.jce.SignatureDSA",
                "com.jcraft.jsch.jce.SignatureECDSA256",
                "com.jcraft.jsch.jce.SignatureECDSA384",
                "com.jcraft.jsch.jce.SignatureECDSA521",
                "com.jcraft.jsch.jce.SignatureRSA",
                "com.jcraft.jsch.jce.TripleDESCBC",
                "com.jcraft.jsch.jce.TripleDESCTR"
            ).forEach(typeName -> hints.reflection().registerType(TypeReference.of(typeName), MemberCategory.values()));
        }
    }
}
