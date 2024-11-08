
package com.board.pds.controller;

import java.io.File;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.board.pds.mapper.PdsMapper;
import com.board.pds.vo.FilesVo;

@RestController // @Controller + @ResponseBody
public class PdsRestController {
	
	@Value("${part4.upload-path}")
	private  String   uploadPath;
	
	@Autowired
	private  PdsMapper  pdsMapper;
	
	// /deleteFile?file_num=${ file.file_num }
	@RequestMapping("/deleteFile")
	public  void   deleteFile(
		@RequestParam  HashMap<String, Object>  map
			) {
		
		// 실제 파일 삭제 sfilename
		Long file_num = 
				Long.parseLong( String.valueOf(map.get("file_num")) );
		FilesVo  fileInfo = pdsMapper.getFileInfo(file_num);
		
		File  file =  new File(uploadPath + fileInfo.getSfilename() );
		if(  file.exists() )
			file.delete();
		
		// files table row  delete
		pdsMapper.deleteUploadFileNum ( map ); 
		
	}
	
	
}
