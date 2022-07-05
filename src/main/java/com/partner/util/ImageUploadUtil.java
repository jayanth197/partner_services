package com.partner.util;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import com.partner.config.PartnerConfigProperties;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImageUploadUtil {

	private PartnerConfigProperties partnerConfigProperties;
	
	public ImageUploadUtil(PartnerConfigProperties partnerConfigProperties) {
		this.partnerConfigProperties = partnerConfigProperties;
	}
	/**
	 * 
	 * @param imgBase64  --> Image data
	 * @param name       --> First Name in the case of Profile and Image name in the case of Product
	 * @param imageType  --> Indicates Profile Image or Product image folder name
	 * @param partnerId  
	 * @param source     --> Profile or Product
	 * @return
	 */
	public String uploadProfileImag(String imgBase64,String name,String imageType,int userId,String source){
		String outputFileName=null;
		try {
			if(!imgBase64.isEmpty()){
				
				byte[] decodedBytes = Base64.getDecoder().decode(imgBase64);
				String uploadFile=null;
				if("PROFILE".equalsIgnoreCase(source)) {
					outputFileName = userId+"_"+name;
					uploadFile = partnerConfigProperties.imageUploadPath+imageType+outputFileName;
				}else if("PROFILE_BG".equalsIgnoreCase(source)) {
					outputFileName = userId+"_bg_"+name;
					uploadFile = partnerConfigProperties.imageUploadPath+imageType+outputFileName;
				}else if("PRODUCTS".equalsIgnoreCase(source)) {
					outputFileName = name;
					uploadFile = partnerConfigProperties.imageUploadPath+imageType+""+userId+"/"+outputFileName;
				}
				
				FileUtils.writeByteArrayToFile(new File(uploadFile), decodedBytes);
				/**
				 * 
				 * ImageIO.setUseCache(false);
				File file = new File(uploadFile);
				FileOutputStream outStream = null;
				BufferedOutputStream bufferredOutputStream = null;
				outStream = new FileOutputStream(file, false);
				bufferredOutputStream = new BufferedOutputStream(outStream);
				bufferredOutputStream.write(decodedBytes);
				bufferredOutputStream.flush();*/
			}
		} catch (IOException e) {
			log.info("Issue while uploading profile pic"+e.getMessage());
		}
		return outputFileName;
	}
}
