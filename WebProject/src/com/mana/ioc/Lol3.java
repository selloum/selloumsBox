package com.mana.ioc;

public class Lol3 {

	@Autowired(name="face")
	private FaceService2 faceService;
	
	public void work() {
		faceService.buy("о╧вс",5);
	}
	
	public FaceService2 getFaceService() {
		return faceService;
	}
}
