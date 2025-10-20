package com.gof.ICNBack.Service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gof.ICNBack.DataSources.Email.EmailDao;
import com.gof.ICNBack.Utils.CodeGenerator;
import com.gof.ICNBack.Utils.Properties;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;

@Component
@Transactional
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    EmailDao emailDao;
    @Autowired
    Environment env;
    @Autowired
    TaskExecutor ioIntensiveExecutor;
    @Autowired
    JavaMailSender sender;
    @Autowired
    Configuration configuration;

    public String generateValidationCode(String email) {
        return CodeGenerator.generateCode(
                env.getProperty("app.mail.code.length", Integer.class, 4));
    }

    public List<String> getValidationCode(String email) {
        return emailDao.getCodeByEmail(email);
    }

    public void cacheValidationCode(String email, String code) {
        emailDao.createRecipe(email, code);
    }

    public Boolean sendCode(String code, String email) {
        // Cache validation code BEFORE sending email
        // This ensures code is available immediately, even if email sending fails
        cacheValidationCode(email, code);

        // Prepare email template values
        HashMap<String, String> values = new HashMap<>();
        values.put("companyName", env.getProperty(Properties.EMAIL_NAME));
        values.put("companyWebsite", env.getProperty(Properties.EMAIL_WEBSITE));
        values.put("validTime", String.valueOf((env.getProperty(Properties.EMAIL_TIMEOUT, int.class, 300) / 60)));
        values.put("verifyCode", code);

        // Send email asynchronously (non-blocking)
        CompletableFuture.supplyAsync(() -> {
            try (StringWriter stringWriter = new StringWriter()) {
                Template template = configuration.getTemplate(env.getProperty(Properties.EMAIL_SEND_CODE_FTL_PATH));
                template.process(values, stringWriter);

                MimeMessage mimeMessage = sender.createMimeMessage();

                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                mimeMessageHelper.setFrom(
                        env.getProperty(Properties.EMAIL_NAME) + '<' + env.getProperty(Properties.EMAIL) + ">");
                mimeMessageHelper.setTo(email);
                mimeMessageHelper.setText(stringWriter.toString(), true);

                sender.send(mimeMessage);
                logger.info("Validation code email sent successfully to: {}", email);
                return true;
            } catch (Exception e) {
                logger.error("Failed to send validation code email to {}: ", email, e);
                throw new RuntimeException(e);
            }
        }, ioIntensiveExecutor);

        return true;
    }
}
