package com.board.pds.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.board.pds.mapper.PdsMapper;
import com.board.pds.service.PdsService;
import com.board.pds.vo.FilesVo;
import com.board.pds.vo.PdsVo;

@Service
public class PdsServiceImpl implements  PdsService {

	// application.properties  의 part4.upload-path 를 가져오기
	// import org.springframework.beans.factory.annotation.Value;
	@Value("${part4.upload-path}")
	private  String   uploadPath;  
	
	@Autowired
	private   PdsMapper   pdsMapper;
	
	@Override
	public List<PdsVo> getPdsList(HashMap<String, Object> map) {
		
		List<PdsVo>  pdsList = pdsMapper.getPdsList( map  );
		
		return       pdsList;
		
	}

	@Override
	public void setWrite(
			HashMap<String, Object> map, 
			MultipartFile[] uploadfiles) {
	   
		// 파일저장 + 자료실 글 쓰기
		// 1. 파일저장
		// uploadfiles []  -> d:\dev\data\
		//String  uploadPath = "d:\\dev\\data\\";
		map.put("uploadPath", uploadPath);
		
		// 2. PdsFile class (별도 생성) - 파일처리 전담 클래스
		System.out.println("PdsFile 호출이전 map:" + map);
		PdsFile.save(map, uploadfiles);
		System.out.println("PdsFile 호출이후 map:" + map);
		
		// 3. Board db 저장 - 새글 등록
		pdsMapper.setWrite(  map ); 
		
		// 4. Files db 저장
		List<FilesVo>  fileList = (List<FilesVo>) map.get("fileList");
		if( fileList.size() > 0  )
			pdsMapper.setFileWriter( map );  // 넘어온 파일여러개 저앚
				
		
	}

	@Override
	public List<PdsVo> getPdsPagingList(HashMap<String, Object> map) {
		
		List<PdsVo>  pdsList = pdsMapper.getPdsList(map);
		
		return       pdsList;
	}

	@Override
	public void setReadcountUpdate(HashMap<String, Object> map) {
		
		pdsMapper.setReadcountUpdate( map );
		
	}

	@Override
	public PdsVo getPds(HashMap<String, Object> map) {
		
		PdsVo   pdsVo   =  pdsMapper.getPds( map ); 
		return  pdsVo;
		
	}

	@Override
	public List<FilesVo> getFileList(HashMap<String, Object> map) {
		List<FilesVo>  fileList = pdsMapper.getFileList(map);
		return         fileList;
	}

	@Override
	public void setUpdate(
			HashMap<String, Object>   map, 
			MultipartFile[]           uploadfiles) {
				
		// 업로드된 파일경로 -> map
		map.put("uploadPath" , uploadPath );
		
		// 업로드된 파일을 폴더에 저장 -> 저장된 정보 -> map
		PdsFile.save( map, uploadfiles);		
		
		// Files table 정보저장 <- map정보를 이용해서
		List<FilesVo>  fileList  =  (List<FilesVo>) map.get("fileList");
		if( fileList.size() > 0  )
			pdsMapper.setFileWriter( map );
		
		// Board table 정보를 저장
		pdsMapper.setUpdate( map  );
		
				
	}

	@Override
	public FilesVo getFileInfo(Long file_num) {
		FilesVo  filesVo  = pdsMapper.getFileInfo( file_num );				
		return   filesVo;
	}

	@Override
	public void setDelete(HashMap<String, Object> map) {
		
		// 1. 해당 파일 정보 조회
		List<FilesVo>  fileList = pdsMapper.getFileList( map );
		
		// 2. idx 에 해당하는 실제 파일들을 삭제
		PdsFile.delete(uploadPath, fileList);
		
		// 3. idx  에 해당하는 Files table 정보 삭제
		pdsMapper.deleteUploadFile( map );
		
		// 4. idx  에 해당하는 board table 정보 삭제
		pdsMapper.setDelete(  map );
		
	}
	
	

}