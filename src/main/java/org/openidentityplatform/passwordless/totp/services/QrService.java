package org.openidentityplatform.passwordless.totp.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class QrService {

    final static String IMG_FORMAT = "png";
    final static Integer SIZE = 300;
    public String generateQr(String data) {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = barcodeWriter.encode(data, BarcodeFormat.QR_CODE, SIZE, SIZE);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, IMG_FORMAT, baos);
            return "data:image/png;base64, ".concat(Base64.encodeBase64String(baos.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
