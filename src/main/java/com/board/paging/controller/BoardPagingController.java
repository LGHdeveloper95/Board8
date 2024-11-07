package com.board.paging.controller;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.board.board.vo.BoardVo;
import com.board.menus.mapper.MenuMapper;
import com.board.menus.vo.MenuVo;
import com.board.paging.mapper.BoardPagingMapper;
import com.board.paging.vo.Pagination;
import com.board.paging.vo.PagingResponse;
import com.board.paging.vo.SearchVo;

@Controller
@RequestMapping("/BoardPaging")
public class BoardPagingController {
	
	@Autowired
	private  MenuMapper         menuMapper;
	
	@Autowired
	private  BoardPagingMapper  boardPagingMapper;
	
	// /BoardPaging/List?nowpage=1&menu_id=MENU01
	@RequestMapping("List")
	public   ModelAndView   list(int nowpage, BoardVo  boardVo) {
		// 메뉴 목록
		List<MenuVo>  menuList =  menuMapper.getMenuList();
			
		//------------------------------------
		// 게시물 목록 조회 (페이징해서)
		//   해당하는 자료수가 1 보다 작으면
		//   응답 데이터에 비어있는 리스트와 null 을 담아 리턴 
		// count : boardVo 안의 menu_id 에 해당하는 총자료수		
		int  count  = boardPagingMapper.count( boardVo );
		System.out.println( count );   // totalRecordCount

	    PagingResponse<BoardVo> response = null;
	    if( count < 1 ) {   // 현재 Menu_id 조회한 자료가 없다면
	    	response = new PagingResponse<>(
	    		Collections.emptyList(), null);
	    	// Collections.emptyList() : 자료는 없는 빈 리스트를 채운다
	    }
	    
	    // 페이징을 위한 초기설정
	    SearchVo  searchVo = new SearchVo();
	    searchVo.setPage(nowpage);   // 현재 페이지 정보
	    searchVo.setRecordSize(10);  // 페이지당 10개
	    searchVo.setPageSize(10);    // paging.jsp 에 출력할 페이지번호수

	    // Pagination 설정
	    Pagination  pagination = new Pagination(count, searchVo);
	    searchVo.setPagination(pagination);
	    //-------------------------------
	    String   menu_id     =  boardVo.getMenu_id();
	  //  String   menu_id     =  "MENU02";
	    
	    // 추가된 조회 옵션들
	    String   title       =  boardVo.getTitle();
	  //  String   title       =  "Spring3";
	    String   writer      =  boardVo.getWriter();
	    String   content     =  boardVo.getContent();
	 // 추가된 조회 옵션들 끌
	    
	    int      offset      =  searchVo.getOffset();
	    int      recordSize  =  searchVo.getRecordSize();
	    
	    List<BoardVo> list = boardPagingMapper.getBoardPagingList(
	    	menu_id, title, writer, content, offset, recordSize );
	    response = new PagingResponse<>(list, pagination);
	    
	    System.out.println(response);
	  	    				
		ModelAndView  mv  =  new  ModelAndView();
		mv.addObject("menuList", menuList);
		
		mv.addObject("menu_id",   menu_id );
		mv.addObject("nowpage",   nowpage );
		
		mv.addObject("searchVo",  searchVo );
		
		mv.addObject("response",  response );
		
		mv.setViewName("boardpaging/list");
		return        mv;
	}	
	
	// 게시글 조회 - 조회수 증가
	// http://localhost:9090/BoardPaging/View?idx=935&menu_id=MENU01&nowpage=7
	@RequestMapping("/View")
	public  ModelAndView  view( int nowpage, BoardVo  boardVo  ) {
		
		// 메뉴 목록 조회
		List<MenuVo>  menuList  =  menuMapper.getMenuList();
		
		// 조회수 증가
		boardPagingMapper.incHit( boardVo );
		
		// idx 로 게시글 조회
		BoardVo       vo        =  boardPagingMapper.getBoard(boardVo); 
		
		ModelAndView  mv = new ModelAndView();
		mv.addObject("menuList", menuList );		
		mv.addObject("vo",       vo );
		mv.addObject("nowpage",  nowpage );
		
		mv.setViewName("boardpaging/view");
		return        mv;
	}
	
	// http://localhost:9090/BoardPaging/WriteForm?menu_id=MENU01&nowpage=1
	@RequestMapping("/WriteForm")
	public  ModelAndView  writeForm(BoardVo boardVo, int nowpage) {
				
		// 메뉴 목록 조회
		List<MenuVo>  menuList  =  menuMapper.getMenuList();
		
		ModelAndView  mv  = new ModelAndView();
		mv.addObject("menuList", menuList);
		mv.addObject("boardVo",  boardVo);
		mv.addObject("nowpage",  nowpage);
		mv.setViewName("boardpaging/write");
		
		return        mv;
	}
	
	//  http://localhost:9090/BoardPaging/Write
	//  nowpage : 1, menu_id=MENU01, title:aaa,	writer:sky, content:aaaa
	@RequestMapping("/Write")
	public  ModelAndView  write( int nowpage, BoardVo boardVo  ) {
		
		// 글저장
		boardPagingMapper.insertBoard( boardVo  );
		
		String        menu_id  =  boardVo.getMenu_id();
		
		ModelAndView  mv       =  new ModelAndView();
		String        fmt      =  "redirect:/BoardPaging/List?menu_id=%s&nowpage=%d";
		String        loc      =  String.format(fmt, menu_id, nowpage);
		mv.setViewName( loc );
		return        mv;
	}
	
	// 게시물 삭제
	// http://localhost:9090/BoardPaging/Delete?idx=1002&menu_id=MENU01&nowpage=1
	@RequestMapping("/Delete")
	public  ModelAndView   delete(BoardVo boardVo, int nowpage) {
		
		// 삭제
		boardPagingMapper.deleteBoard( boardVo  );
		
		String        menu_id =  boardVo.getMenu_id();
		
		ModelAndView  mv      =  new ModelAndView();
		String        fmt     =  "redirect:/BoardPaging/List?menu_id={0}&nowpage={1}";
		String        loc     =  MessageFormat.format(fmt, menu_id, nowpage);
		mv.setViewName( loc );
		return        mv;
	}
	
	// http://localhost:9090/BoardPaging/UpdateForm?idx=996&menu_id=MENU01&nowpage=1
	@RequestMapping("/UpdateForm")
	public  ModelAndView   updateForm(BoardVo boardVo, int nowpage) {
		
		List<MenuVo>  menuList = menuMapper.getMenuList();
		
		BoardVo   vo       =  boardPagingMapper.getBoard( boardVo );   // idx
		String    menu_id  =  boardVo.getMenu_id();
		
		ModelAndView  mv = new ModelAndView();
		// 메뉴목록 조회
		mv.addObject("menuList",  menuList);
		// 수정할 데이터 (vo)
		mv.addObject("vo",      vo);
		mv.addObject("menu_id", menu_id );
		mv.addObject("nowpage", nowpage );
		mv.setViewName("boardpaging/update");
		return        mv;
	}
	
	// http://localhost:9090/BoardPaging/Update
	// 수정용 (idx, title, content), 페이지이동 (menu_id, nowpage)
	@RequestMapping("/Update")
	public  ModelAndView   update( BoardVo boardVo, int nowpage  ) {
		
		// 자료를 수정
		boardPagingMapper.updateBoard( boardVo  );
		
		String        menu_id =  boardVo.getMenu_id();
		
		ModelAndView  mv      =  new ModelAndView();
		String        fmt     = "redirect:/BoardPaging/List?menu_id={0}&nowpage={1}";
		String        loc     = MessageFormat.format(fmt, menu_id, nowpage);
		mv.setViewName(  loc );
		
		return        mv;
	}
	
	
}















