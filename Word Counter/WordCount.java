import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WordCount {
	
	/*
	 * Wrapper class to be used for max heap.
	 * String word: A word appearing in the file.
	 * int freq: the number of times the word appears in the file.
	 */
	private class WordFreq{
		private String word;
		private int freq;
		
		public WordFreq(String w, int f){
			word = w; freq = f;
		}
	}
	
	PriorityQueue<WordFreq> frequencies; // max heap
	
	HashMap<String, Integer> cache;
	
	public WordCount(String f){
		
		frequencies = new PriorityQueue<WordFreq>(new Comparator<WordFreq>(){
			@Override
			public int compare(WordFreq a, WordFreq b){
				if(a.freq < b.freq){return 1;}
				else if(a.freq == b.freq){return 0;}
				else{return -1;}
			}
		});
		
		cache = new HashMap<String, Integer>();
		
		try{
			if(f != null){
				Scanner story = new Scanner(new File(f));
				
				while(story.hasNext()){
					String cur = story.next();
					// Removing non letters from beginning and end of string and making every char lower case.
					cur = cur.replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "").toLowerCase();
					
					if(cur.length() != 0){
						cache.put(cur, cache.getOrDefault(cur, 0)+1);
					}
				}
				
				// Adding to heap to find top used words.
				for(String cur : cache.keySet()){
					WordFreq entry = new WordFreq(cur, cache.get(cur));
					frequencies.add(entry);
				}
				
				story.close();
			}
		} catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
		
		
	}
	
	// Prints up to the n most common words still left in the heap.
	public void printCommon(int n){
		int last = 0;
		for(int i = 0 ; i < n ; i++){
			if(frequencies.isEmpty()){return;}
			WordFreq res = frequencies.poll();
			String end = res.freq == 1 ? "time." : "times.";
			System.out.println("'" + res.word + "'" + " appears " + res.freq + " " + end);
			if(i == n-1){last = res.freq;}
		}
		while(!frequencies.isEmpty() && frequencies.peek().freq == last){
			WordFreq res = frequencies.poll();
			String end = res.freq == 1 ? "time." : "times.";
			System.out.println("'" + res.word + "'" + " appears " + res.freq + " " + end);
		}
	}
	
	
	public static void main(String[] args){
		WordCount counter = new WordCount(new String("ShortStory.txt"));
		counter.printCommon(5);
	}

}
