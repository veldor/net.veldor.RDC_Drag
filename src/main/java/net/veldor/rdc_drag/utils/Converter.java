package net.veldor.rdc_drag.utils;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Converter {
    public static List<File> convertFiles(List<File> inputFiles) {
        List<File> outputFiles = new ArrayList<>();
        for (File f :
                inputFiles) {
            // проверю тип файла, если он PDF- отдам на выход, если .doc или .docx- конвертирую в .pdf
            // и отдам на выход его. Если файл левый- пропущу
            if(f.isFile()){
                if(f.getName().endsWith(".pdf")){
                    outputFiles.add(f);
                }
                else if(f.getName().endsWith(".doc")){
                    File convertedFile = convertDoc(f);
                    if(convertedFile != null && convertedFile.length() > 0){
                        outputFiles.add(convertedFile);
                    }
                }
                else if(f.getName().endsWith(".docx")){
                    File convertedFile = convertDocx(f);
                    if(convertedFile != null && convertedFile.length() > 0){
                        outputFiles.add(convertedFile);
                    }
                }
            }
        }
        return outputFiles;
    }

    private static File convertDoc(File f) {
        // создам файл во временной папке
        try {
            File tempFile = getTempFile();
            // сначала попробую конвертацию с помощью MS Word
            try (InputStream docxInputStream = new FileInputStream(f); OutputStream outputStream = new FileOutputStream(tempFile)) {
                IConverter converter = LocalConverter.builder().build();
                converter.convert(docxInputStream).as(DocumentType.MS_WORD).to(outputStream).as(DocumentType.PDF).execute();
                System.out.println("file converted by MS Word");
            }
            catch (Throwable e){
                // конвертация не удалась, попробую сконвертировать файл с помощью OpenOffice
                LocalOfficeManager officeManager = LocalOfficeManager.install();
                try {
                    officeManager.start();
                    JodConverter.convert(f).to(tempFile).execute();
                    System.out.println("file converted by open office");
                }
                catch (Throwable t){
                    System.out.println("can't convert doc by office");
                }
                finally {
                    OfficeUtils.stopQuietly(officeManager);
                }
            }
            if(tempFile.length() > 0){
                return tempFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static File convertDocx(File f) {
        // создам файл во временной папке
        try {
            File tempFile = getTempFile();
            // сначала попробую конвертацию с помощью MS Word
            try (InputStream docxInputStream = new FileInputStream(f); OutputStream outputStream = new FileOutputStream(tempFile)) {
                IConverter converter = LocalConverter.builder().build();
                converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
                System.out.println("file converted by MS Word");
            }
            catch (Throwable e){
                // конвертация не удалась, попробую сконвертировать файл с помощью OpenOffice
                LocalOfficeManager officeManager = LocalOfficeManager.install();
                try {
                    officeManager.start();
                    JodConverter.convert(f).to(tempFile).execute();
                    System.out.println("file converted by open office");
                }
                catch (Throwable t){
                    System.out.println("can't convert doc by office");
                }
                finally {
                    OfficeUtils.stopQuietly(officeManager);
                }
            }
            if(tempFile.length() > 0){
                return tempFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File getTempFile() throws IOException {
        return File.createTempFile(new RandomString().nextString(), ".pdf");
    }
}
