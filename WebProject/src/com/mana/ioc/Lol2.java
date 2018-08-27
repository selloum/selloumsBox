package com.mana.ioc;

public class Lol2 {

	@Autowired(value=FaceService2.class)
	private FaceService2 faceService;
	
	public void work() {
		faceService.buy("µÂÂê",5);
	}
	
	public FaceService2 getFaceService() {
		return faceService;
	}
}
