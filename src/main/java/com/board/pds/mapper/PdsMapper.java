package com.board.pds.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.board.pds.vo.FilesVo;
import com.board.pds.vo.PdsVo;

@Mapper
public interface PdsMapper {

	List<PdsVo> getPdsList(HashMap<String, Object> map);
	List<FilesVo> getFileList(HashMap<String, Object> map);
	void setWrite(HashMap<String, Object> map);
	void setFileWriter(HashMap<String, Object> map);
	void setReadcountUpdate(HashMap<String, Object> map);
	void setUpdate(HashMap<String, Object> map);
	int count(Object object);
	PdsVo getPds(HashMap<String, Object> map);
	FilesVo getFileInfo(Long file_num);

}
