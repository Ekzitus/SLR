package sample;

public class Production {
	
	private String left;
	private String rigth;

	Production(String left, String rigth) {
		this.setLeft(left);
		this.setRigth(rigth);
	}

	/**
	 * @return the left
	 */
	public String getLeft() {
		return left;
	}

	/**
	 * @param left the left to set
	 */
	public void setLeft(String left) {
		this.left = left;
	}

	/**
	 * @return the rigth
	 */
	public String getRigth() {
		return rigth;
	}

	/**
	 * @param rigth the rigth to set
	 */
	public void setRigth(String rigth) {
		this.rigth = rigth;
	}

}
