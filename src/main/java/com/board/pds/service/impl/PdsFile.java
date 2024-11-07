package com.board.pds.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.board.pds.vo.FilesVo;

public class PdsFile {

	// uploadfiles 에 넘어온 파일들을 저장
	public static void save(
			HashMap<String, Object> map, 
			MultipartFile[] uploadfiles) {
		
		// 저장될 경로를 가져온다
		String  uploadPath = String.valueOf( map.get("uploadPath") );
		
		// 파일들을 저장하고 Files table 에 저장할 정보를 map 에 담는다 
		List<FilesVo>  fileList = new ArrayList<>();
		
		// 파일별로 반북
		for (MultipartFile uploadfile : uploadfiles) {
			if( uploadfile.isEmpty() )
				continue;
			
			String  orgName     = uploadfile.getOriginalFilename();
			// d:\\dev\\data\\data.abc.txt
			String  fileName    = 
				( orgName.lastIndexOf("/") < 0 )  
				?   orgName
				:	orgName.substring( orgName.lastIndexOf("/") + 1 );  // data.abc.txt 
			String  fileExt     = 
				(orgName.lastIndexOf('.') < 0) 
				?  " "
				:  orgName.substring( orgName.lastIndexOf('.')  )     ;  // .txt
			
			// 날짜 폴더 생성   d:\\dev\\data\\2024\\11\\05
			String  folderPath = makeFolder( uploadPath );
			
			// 파일 중복방지 : 같은 폴더에 같은 파일명의 파일을 저장하면 마지막 파일만 남는다
			// 중복되지 않는 고유한 문자열 생성 :  UUID
			String  uuid       = UUID.randomUUID().toString();
			
			//  File.separator (/, \\)
			String  saveName   = uploadPath + File.separator
					           + folderPath + File.separator
			                   + uuid       + " " + fileName;
			String  saveName2  = folderPath + File.separator
					           + uuid       + " " + fileName;
			
			Path    savePath   = Paths.get(saveName);
			//  import java.nio.file.Path
			//  Paths.get() : 특정 경로의 파일정보를 가져온다
			
			//  파일저장
			try {
				uploadfile.transferTo(savePath);  // 업로드 폴더에 파일을 저장
				System.out.println( savePath +  "가 저장됨" );
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} // try catch end 
			
			// 저장된 파일들의 정보를 map 에  List 방식으로 저장 -> pdsServiceImpl 에 전달 
			FilesVo  vo = new FilesVo(0, 0, fileName, fileExt, saveName2);
			fileList.add( vo );
			
		}  // for end
		
		// 돌려줄 정보 map 저장
		map.put("fileList", fileList);
	}

	// 날짜 폴더 생성   d:\\dev\\data\\2024\\11\\05
	private static String makeFolder(String uploadPath) {
		// d:\\dev\\data +  \\2024\\11\\05
		// uploadPath    +  folderPath
		
		String  dateStr     =  LocalDate.now().format(
				DateTimeFormatter.ofPattern("yyyy/MM/dd") );
		String  folderPath  =  dateStr.replace("/", File.separator ); 
		
		File    uploadPathFolder = new File(uploadPath, folderPath);
		
		if(uploadPathFolder.exists() == false ) {
			uploadPathFolder.mkdirs();  // make directory
			// mkdir()   :  상위폴더가  없으면 폴더 생성실패 
			// mkdirs()  :  상위폴더    없어도 폴더전체를 생성한다
		} 
		
		return folderPath;
	}
	
	// 실제 물리파일 삭제 : 여러파일 삭제
	public static void delete(
		String uploadPath, List<FilesVo> fileList  ) {
		
		String  path  = uploadPath;
		
		fileList.forEach( ( file ) -> {
			String  sfile  = file.getSfilename();  // 실제 파일명 uuid
			
			File    dfile  = new  File( path + sfile );
			if( dfile.exists() )
			   dfile.delete();
			
		} ); 
		
	}

}











