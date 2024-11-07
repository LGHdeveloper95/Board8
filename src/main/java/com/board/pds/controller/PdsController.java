package com.board.pds.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.board.board.vo.BoardVo;
import com.board.menus.mapper.MenuMapper;
import com.board.menus.vo.MenuVo;
import com.board.paging.vo.Pagination;
import com.board.paging.vo.PagingResponse;
import com.board.paging.vo.SearchVo;
import com.board.pds.mapper.PdsMapper;
import com.board.pds.service.PdsService;
import com.board.pds.vo.FilesVo;
import com.board.pds.vo.PdsVo;

@Controller
@RequestMapping("/Pds")
public class PdsController {
	
	@Autowired
	private  MenuMapper   menuMapper;
	
	@Autowired
	private  PdsService   pdsService;
	
	@Autowired
	private  PdsMapper    pdsMapper;
	
	// /Pds/List?nowpage=1&menu_id=MENU01
	@RequestMapping("/List")
	public  ModelAndView  list(
			@RequestParam  HashMap<String, Object>  map) {
		System.out.println(map);    // {nowpage=1, menu_id=MENU01}
		
		List<MenuVo>  menuList = menuMapper.getMenuList();
		
		// 자료실 목록 조회	( data list : 10개 record)		
		// 전체 record 수를 조회
		String   menu_id       =  String.valueOf( map.get("menu_id") );
		int      count         =  pdsMapper.count( menu_id  );  // totalRecordCount
		
		// 자료수기 0 일 때 처리
		PagingResponse<PdsVo> response = null;
	    if( count < 1 ) {   // 현재 Menu_id 조회한 자료가 없다면
	    	response = new PagingResponse<>(
	    		Collections.emptyList(), null);
	    	// Collections.emptyList() : 자료는 없는 빈 리스트를 채운다
	    }
	    
	    // 페이징을 위한 초기설정
	    SearchVo  searchVo   =  new SearchVo();
	    int       nowpage    =  Integer.parseInt( String.valueOf( map.get("nowpage")) ); 
	    searchVo.setPage( nowpage );   // 현재 페이지 정보
	    searchVo.setRecordSize(10);  // 페이지당 10개
	    searchVo.setPageSize(10);    // paging.jsp 에 출력할 페이지번호수

	    // Pagination 설정
	    Pagination  pagination = new Pagination(count, searchVo);
	    searchVo.setPagination( pagination );
	    //-------------------------------
	    // menu_id,  nowpage, 
	    // title, writer, content : 검색어 주제 
	    // offset, recordSize 		
		int     offset        =  searchVo.getOffset();
		int     recordSize     =  searchVo.getRecordSize();
			
		// String  title  = String.valueOf( map.get("title") ); // 조회안됨
		map.put("search",      map.get("search") );
		map.put("searchtext",  map.get("searchtext") );
		
		map.put("offset",      offset);		
		map.put("recordSize",  recordSize);
		System.out.println("map:" + map  );
		List<PdsVo>   pdsList  = pdsService.getPdsPagingList( map );  // menu_id, nowpage
		
		response = new PagingResponse<PdsVo>(pdsList, pagination);
		System.out.println("PDSController List response=" + response );
				
		ModelAndView   mv = new ModelAndView();
		mv.addObject("menuList",  menuList );		
		// mv.addObject("pdsList",   pdsList   );
		mv.addObject("searchVo",  searchVo  );
		mv.addObject("response",  response );
		mv.addObject("map",       map       );
		mv.setViewName("pds/list");
		return         mv;
	}
	
	// /Pds/WriteForm?nowpage=1&menu_id=MENU01
	@RequestMapping("/WriteForm")
	public  ModelAndView   writeForm(
		@RequestParam   HashMap<String, Object> map	) {
		
		List<MenuVo>  menuList  =  menuMapper.getMenuList();
		
		ModelAndView  mv  =  new ModelAndView();
		mv.addObject( "menuList",  menuList );
		mv.addObject( "map",       map      );
		
		mv.setViewName("pds/write");
		return        mv;
	}
	
	// /Pds/Write : 자료실 저장
	//  menu_id, nowpage, title, writer, content +  upload 몇개의 파일
	//  map              :  menu_id, nowpage, title, writer, content 
	//  MultipartFile [] :  upload 몇개의 파일 ( <- HttpServletRequest(request))
	@PostMapping("/Write")
	public  ModelAndView  write(
		@RequestParam                  HashMap<String, Object>  map,
		@RequestParam(value="upfile")  MultipartFile []         uploadfiles
			) {
		System.out.println("map:"         + map);
		System.out.println("uploadfiles:" + uploadfiles);
		
		//  저장
		// 1. map 정보
		// 새글 추가 -> db : Board table 저장
		// 2. MultipartFile [] 정보저장
		// 2-1. 실제폴더에 파일 저장(중복파일처리)  -> uploadPath( d:\dev\data\ )
		// 2-2. 저장된파일정보를 DB 에 저장 -> Files table 에 
		pdsService.setWrite( map,  uploadfiles );
		
		ModelAndView  mv  = new ModelAndView();
//		String   fmt = "redirect:/Pds/List?menu_id=%s&nowpage=%d";
//		String   loc = String.format(fmt,
//				 String.valueOf(map.get("menu_id")), 
// 				 Integer.parseInt(String.valueOf(map.get("nowpage"))) ); 
		String   fmt = "redirect:/Pds/List?menu_id=%s&nowpage=%s";
		String   loc = String.format(fmt,
				map.get("menu_id"), 
				map.get("nowpage") ); 
		mv.setViewName( loc );
		return        mv;		
	} 
	
	//
	@RequestMapping("/View")
	public ModelAndView view(
		@RequestParam HashMap<String,Object> map ) {
		// 메뉴목록
		List<MenuVo> menuList = menuMapper.getMenuList();
		
		// 조회수 증가
	    pdsService.setReadcountUpdate(map); // map : idx
	    
		// 조회할 자료실 게시물 정보 (idx)
	    PdsVo pdsVo = pdsService.getPds(map);
	    String content = pdsVo.getContent().replace("\n", "<br>");
		pdsVo.setContent(content);

	   
	    
	    
		// 조회할  파일 정보
		List<FilesVo> fileList = pdsService.getFileList(map);
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("menuList",menuList);
		mv.addObject("vo", pdsVo );
		mv.addObject("fileList", fileList );
		mv.addObject("map", map );
		mv.setViewName("pds/view");
		return mv;
		
	}
    
	
}











