package com.bog.ecommerce.service;

import com.bog.ecommerce.exception.CustomException;
import com.bog.ecommerce.model.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    public String generateUUID() {
        return UUID.randomUUID().toString().split("-")[4];
    }

    public boolean createDirs(String folder) {
        File mainDirectory = new File(Path.fullPAth());
        File directory = new File(Path.fullPAth() + folder);
        boolean dirCheck1 = true;
        boolean dirCheck2 = true;
        if (!mainDirectory.exists()) dirCheck1 = mainDirectory.mkdirs();
        if (!directory.exists()) dirCheck2 = directory.mkdirs();
        return dirCheck1 && dirCheck2;
    }

    //todo thumbnail?
    public String uploadImage(MultipartFile file, String folder) throws IOException, CustomException {
        if (!createDirs(folder)) throw new CustomException("couldn't create directory");
        //todo is gif check necessary?
        if (ImageIO.read(file.getInputStream()) == null) {
            log.warn("file isn't image type");
            throw new CustomException("file isn't image type");
        }
        String randomId = generateUUID();
        String[] splitedName = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        String hashedName = splitedName[0] + "-" + randomId + "." + splitedName[1];
        String path = Path.fullPAth() + folder + Objects.requireNonNull(hashedName);

        File serverFile = new File(path);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(file.getBytes());
        stream.close();

        return Path.IMAGE_DIR + serverFile.getName();
    }


    public void deleteFile(String imageName) {
        File oldFile = new File(Path.fullPAth() + imageName);
        if (oldFile.exists()) {
            if (!oldFile.delete()) log.error("file couldn't be deleted");
            log.info("file successfully deleted");
        }
    }
}
