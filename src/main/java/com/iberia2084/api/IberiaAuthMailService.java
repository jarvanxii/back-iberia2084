package com.iberia2084.api;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class IberiaAuthMailService {
    private final JavaMailSender mailSender;
    private final String from;
    private final String fromName;
    private final String replyTo;
    private final long signupTtlMinutes;
    private final long recoveryTtlMinutes;

    public IberiaAuthMailService(
            JavaMailSender mailSender,
            @Value("${iberia2084.mail.from:no-reply@iberia2084.com}") String from,
            @Value("${iberia2084.mail.from-name:Iberia 2084}") String fromName,
            @Value("${iberia2084.mail.reply-to:no-reply@iberia2084.com}") String replyTo,
            @Value("${iberia2084.auth.signup.code-ttl-minutes:15}") long signupTtlMinutes,
            @Value("${iberia2084.auth.password-recovery.token-ttl-minutes:30}") long recoveryTtlMinutes) {
        this.mailSender = mailSender;
        this.from = from;
        this.fromName = fromName;
        this.replyTo = replyTo;
        this.signupTtlMinutes = signupTtlMinutes;
        this.recoveryTtlMinutes = recoveryTtlMinutes;
    }

    public void sendSignupCode(String email, String displayName, String code) {
        var recipientName = displayName(displayName);
        var ttl = Math.max(1, signupTtlMinutes);
        var plainText = """
                Hola %s,

                Tu código para crear la cuenta en Iberia 2084 es: %s

                Caduca en %d minutos. Si no has solicitado este acceso, ignora este correo.

                Iberia 2084
                """.formatted(recipientName, code, ttl);
        var html = shell(
                "Verificación de cuenta",
                "Confirma tu alta en Iberia 2084",
                "Hola %s. Usa este código para validar tu correo y crear tu cuenta de mando."
                        .formatted(escape(recipientName)),
                """
                <tr>
                  <td style="padding:0 0 22px;">
                    <div style="border:1px solid rgba(221,185,103,.42);border-radius:8px;background:#090b0a;padding:18px 16px;text-align:center;">
                      <div style="color:#9ca3a0;font-size:12px;font-weight:700;text-transform:uppercase;">Código de verificación</div>
                      <div style="padding-top:10px;color:#f1d68a;font-size:34px;line-height:1;font-weight:900;letter-spacing:8px;">%s</div>
                    </div>
                  </td>
                </tr>
                """.formatted(escape(code)),
                "El código caduca en %d minutos. Si no has solicitado este registro, puedes ignorar este correo."
                        .formatted(ttl));

        sendHtml(email, "Código de verificación Iberia 2084", plainText, html);
    }

    public void sendPasswordResetLink(String email, String displayName, String resetUrl) {
        var recipientName = displayName(displayName);
        var ttl = Math.max(1, recoveryTtlMinutes);
        var plainText = """
                Hola %s,

                Hemos recibido una solicitud para recuperar el acceso a Iberia 2084.

                Abre este enlace para crear una nueva contraseña:
                %s

                El enlace caduca en %d minutos. Si no has solicitado esta recuperación, ignora este correo.

                Iberia 2084
                """.formatted(recipientName, resetUrl, ttl);
        var html = shell(
                "Recuperación de acceso",
                "Crea una nueva contraseña",
                "Hola %s. Hemos recibido una solicitud para recuperar el acceso a tu cuenta."
                        .formatted(escape(recipientName)),
                """
                <tr>
                  <td style="padding:0 0 22px;text-align:center;">
                    <a href="%s" style="display:inline-block;border-radius:6px;background:#d7ad56;color:#10120f;font-size:15px;font-weight:900;line-height:48px;min-width:240px;text-align:center;text-decoration:none;">Restablecer contraseña</a>
                  </td>
                </tr>
                <tr>
                  <td style="padding:0 0 18px;color:#9ca3a0;font-size:12px;line-height:1.6;">
                    Si el botón no funciona, abre este enlace:<br>
                    <a href="%s" style="color:#f1d68a;word-break:break-all;">%s</a>
                  </td>
                </tr>
                """.formatted(escape(resetUrl), escape(resetUrl), escape(resetUrl)),
                "El enlace caduca en %d minutos. Si no has solicitado esta recuperación, no tienes que hacer nada."
                        .formatted(ttl));

        sendHtml(email, "Recuperación de acceso Iberia 2084", plainText, html);
    }

    private void sendHtml(String email, String subject, String plainText, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(plainText, html);
            helper.setFrom(new InternetAddress(from, fromName, "UTF-8"));
            if (replyTo != null && !replyTo.isBlank()) {
                helper.setReplyTo(replyTo);
            }
            mailSender.send(message);
        } catch (MailException | MessagingException | UnsupportedEncodingException exception) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo enviar el correo de acceso.");
        }
    }

    private String shell(String kicker, String title, String intro, String bodyRows, String footer) {
        return """
                <!doctype html>
                <html>
                  <body style="margin:0;background:#080a09;color:#eef0e9;font-family:Arial,Helvetica,sans-serif;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#080a09;padding:24px 12px;">
                      <tr>
                        <td align="center">
                          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:560px;border:1px solid rgba(221,185,103,.22);background:#10130f;">
                            <tr>
                              <td style="padding:24px 24px 8px;color:#d7ad56;font-size:12px;font-weight:900;text-transform:uppercase;">%s</td>
                            </tr>
                            <tr>
                              <td style="padding:0 24px 12px;color:#f4f0dc;font-size:26px;line-height:1.1;font-weight:900;">%s</td>
                            </tr>
                            <tr>
                              <td style="padding:0 24px 22px;color:#c4c8bd;font-size:14px;line-height:1.6;">%s</td>
                            </tr>
                            %s
                            <tr>
                              <td style="padding:0 24px 24px;color:#9ca3a0;font-size:12px;line-height:1.6;">%s</td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(escape(kicker), escape(title), intro, bodyRows, escape(footer));
    }

    private String displayName(String value) {
        return value == null || value.isBlank() ? "operador" : value.trim();
    }

    private String escape(String value) {
        return HtmlUtils.htmlEscape(value == null ? "" : value);
    }
}
