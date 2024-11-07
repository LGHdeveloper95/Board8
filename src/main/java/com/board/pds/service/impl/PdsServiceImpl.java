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
		PdsVo  pdsVo = pdsMapper.getPds( map ); 
		return pdsVo;
	}

	@Override
	public List<FilesVo> getFileList(HashMap<String, Object> map) {
		List<FilesVo> fileList = pdsMapper.getFileList(map);
		return fileList;
	}

}




