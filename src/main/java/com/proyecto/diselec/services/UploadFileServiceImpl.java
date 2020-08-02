package com.proyecto.diselec.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class UploadFileServiceImpl implements UploadFileService{

    //Definicion del folder donde se guardaran los datos
    public static final String UPLOADS_FOLDER = "uploads";


    @Override
    public Resource load(String filename) throws MalformedURLException {
        Path pathFoto = getPath(filename);
        Resource recurso = new UrlResource(pathFoto.toUri());

        if (!recurso.exists() || !recurso.isReadable()){
            throw new RuntimeException("Error: no se puede cargar la imagen " + pathFoto.toString());
        }
        return recurso;
    }

    @Override
    public String copy(MultipartFile file) throws IOException {
        //Se crea un nuevo nombre para el archivo
        String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Busca la ruta raiz para asignar al nuevo archivo
        Path rootPath = getPath(uniqueFilename);

        //Lo copia a la ruta
        Files.copy(file.getInputStream(), rootPath);
        return uniqueFilename;
    }

    @Override
    public boolean delete(String filename) {
        // Verifica el nombre del archivo
        if (filename != null && filename.length() > 0){
            //Extrae la ruta para asignar al archivo
            Path rootPath = getPath(filename);
            //Busca el archivo
            File archivo = rootPath.toFile();

            //Si existe y se puede leer, procede a borrarlo
            if (archivo.exists() && archivo.canRead()){
                return archivo.delete();
            }
        }
        return false;
    }


    public Path getPath(String filename){
        return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
    }
}
