package com.mana.ioc;

public class Lol {

	@Autowired
	private FaceService faceService;
	
	public void work() {
		faceService.buy("С����",5);
	}
	
	public FaceService getFaceService() {
		return faceService;
	}
}
