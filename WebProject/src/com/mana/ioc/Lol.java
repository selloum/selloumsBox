package com.mana.ioc;

public class Lol {

	@Autowired
	private FaceService faceService;
	
	public void work() {
		faceService.buy("–°”„»À",5);
	}
	
	public FaceService getFaceService() {
		return faceService;
	}
}
