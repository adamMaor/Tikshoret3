package View;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Client_Server {
	private static HashMap<Integer, String> ingredients = new HashMap<>();

	public static void main(String[] args) {
		

		int i = 0;

		String part[] = {};

		try (BufferedReader br = new BufferedReader(new FileReader("inventory.txt"))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				part = sCurrentLine.split(" ");
				ingredients.put(Integer.parseInt(part[0]), part[1]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		new Client(1).start();

		while (i++ < Constants.NUM_OF_CLIENTS) {
			new Client(i).start();
		}
	}
	
	public static String getIngredientName(int ingredientNumber){
		if (ingredients.containsKey(ingredientNumber)){
			return ingredients.get(ingredientNumber);
		}
		return null;
	}

}
