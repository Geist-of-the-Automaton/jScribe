/** 
 * @author Auden Childress
 * @version final
 * @since March, 2018
 * Copyright 2019
 * GeistfulAutomaton@gmail.com
 */

package jScribe_final;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Scanner;

public class Words 
{
	private static Word heads[] = new Word[35];
	private Word lastCap;
	private class Word
	{
		public Word next;
		public Word nCap;
		public String word;
		public Word (String w)
		{
			if (w.charAt(0) == w.toUpperCase().charAt(0))
			{
				if (lastCap != null)
					lastCap.nCap = this;
				lastCap = this;
			}
			word = w;
		}
	}
	private void insert (int head, String w)
	{
		Word temp = heads[head];
		while (temp.next != null)
			temp = temp.next;
		temp.next = new Word (w);
	}
	
	public void add (String w)
	{
		char c = w.toLowerCase().charAt(0);
		Word toAdd = new Word (w);
		boolean isCap = lastCap == toAdd;
		for (Word h : heads)
		{
			boolean b = h.word.toLowerCase().charAt(0) == c;
			for (Word temp = h; temp != null && b; temp = temp.next)
			{
				if (temp.nCap != null)
					lastCap = temp;
				if (temp.next == null)
				{
					temp.next = toAdd;
					if (isCap)
					{
						Word tmp = lastCap.nCap;
						lastCap.nCap = toAdd;
						toAdd.nCap = tmp;
					}
					return;
				}
				else if (temp.next.word.compareTo(w) > 0)
				{
					Word tmp = temp.next;
					temp.next = toAdd;
					toAdd.next = tmp;
					if (isCap)
					{
						Word t = lastCap.nCap;
						lastCap.nCap = toAdd;
						toAdd.nCap = t;
					}
					return;
				}
			}
		}
	}
	
	public Words ()
	{
		importDict();
	}
	
	private void importDict ()
	{
		try 
		{
			char c = ' ';
			Scanner s = new Scanner(new File(".js_dict"));
			int i = 0;
			//double l = 0;
			//double words = 0;
			while (s.hasNextLine() && i < heads.length)
			{
				String w = s.nextLine();
				//l += w.length();
				//words++;
				char z = w.toLowerCase().charAt(0);
				if (Character.isLetterOrDigit(z))
				{
					if (z == c)
						insert(i, w);
					else
					{
						if (heads[i] != null)
							i++;
						if (i >= heads.length)
							break;
						heads[i] = new Word (w);
						c = z;
					}
				}
			}
			//double d = l / words;
			//System.out.println("there are " + words + " words with average length of " + d + " characters");
			if (i == 0)
			{
				createDict();
			}
			s.close();
		} 
		catch (Exception e) 
		{ 
			createDict();
		}
	}
	
	private void createDict ()
	{
		try
		{
			Scanner dict = new Scanner(getClass().getResourceAsStream("/dict.txt"));
			PrintWriter pw = new PrintWriter(new FileOutputStream(".js_dict"), false);
			while (dict.hasNextLine())
				pw.println(dict.nextLine());
			pw.close();
			dict.close();
		}
		catch (Exception f)
		{ }
		importDict();
	}
	
	public void save ()
	{
		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(".js_dict"), false);
			for (Word h : heads)
				for (Word temp = h; temp != null; temp = temp.next)
					pw.println(temp.word);
			pw.close();
		}
		catch (Exception e)
		{ 
			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream("error.txt"), false);
				pw.println(e.toString());
				pw.close();
			}
			catch (Exception f)
			{ 
				e.printStackTrace();
			}
		}
	}
	
	public void suggs (String s, LinkedList <String> alt)
	{
		String proper = getRep(s);
		for (Word w : heads)
		{
			int b = w.word.toLowerCase().charAt(0) == s.charAt(0) ? 1 : 0;
			for (Word temp = w; temp != null && b == 1; temp = temp.next)
			{
				if (proper.equals(getRep(temp.word)))
					alt.add(temp.word);
			}
		}
	}
	
	public void getList (String w, LinkedList <String> al)
	{
		for (Word h : heads)
			for (Word temp = h; temp != null; temp = temp.next)
			{
				if (Compare(w, temp.word) >= 0)
					al.add(temp.word);
			}
	}
	
	public int isWord (String s)
	{
		for (Word w : heads)
		{
			s = s.toLowerCase();
			int b = w.word.toLowerCase().charAt(0) == s.charAt(0) ? 1 : 0;
			for (Word temp = w; temp != null && b == 1; temp = temp.next)
			{
				String l = temp.word.toLowerCase();
				if (s.equals(temp.word.toLowerCase()))
					return 1;
				else if (s.compareTo(l) < 0)
					return isNumber(s);
			}
		}
		return 0;
	}
	
	private static int isNumber (String s)
	{
		try 
		{
			long l = Long.parseLong(s);
			String s2 = l + "";
			return s.equals(s2) ? 1 : 0;
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	
	public static int isWS(char c)
	{
		char arr[] = {'\n', '\t'};
		for (char b : arr)
			if (b == c)
				return 1;
		return 0;
	}
	
	public static int isAWS (char c)
	{
		return isWS(c) == 1 || c == ' ' ? 1 : 0;
	}
	
	public static int isGrammer (char a)
	{
		char grammer[] = {' ','.',',',';','?',':','!','\n'};
		for (char c : grammer)
			if (a == c)
				return 1;
		return 0;
	}
	
	private static boolean isVowel (char c)
	{
		return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
	}
	
	private static int getVal (char arr[])
	{
		// this along with substitutions (when 2) will give transpositions,
		// otherwise it acts as a good filter for general numerical similarity
		int val = 0;
		for  (char c : arr)
			val += c;
		return val;
	}
	
	public static String reverseCase (String s)
	{
		char izard[] = s.toCharArray();
		String ret = "";
		for (char mander : izard)
		{
			boolean b = Character.isAlphabetic(mander);
			char c = (char) (b && mander > 96 ? mander - 32 : b && mander < 91 ? mander + 32 : mander);
			ret += c;
		}
		return ret;
	}
	
	public static String capEvery (String s)
	{
		char arr[] = s.toCharArray();
		String r = "";
		for (int i = 0; i < arr.length; i++)
		{
			boolean b = arr[i] == '\n' || arr[i] == ' ';
			if (i + 1 < arr.length)
				b = b && Character.isAlphabetic(arr[i + 1]);
			char c = arr[i];
			if (b) 
			{
				c = (char) (arr[i + 1] > 96 ? arr[i + 1] - 32 : c);
				if (c != arr[i])
					r += arr[i];
				else
					b = false;
			}
			r += c;
			if (b)
				i++;
		}
		return r;
	}
	
	public static int getLineCount (String s, int t, int lineLen)
	{			
		String rep = "a";
		for (int i = 1; i < t - 1; i++)
			rep += 'a';
		rep += " ";
		while (s.contains("\t"))
			s = s.replaceAll("\t", rep);
		while (s.contains("  "))
			s = s.replaceAll("  ", "a ");
		while (s.contains("\n\n"))
			s = s.replaceAll("\n\n", "\nauden\n");
		String p[] = s.split("\n");
		int lines = 0;
		for (String a : p)
			lines += getLines(a, rep, lineLen);
		return lines;
	}
	
	private static int getLines(String s, String rep, int lineLen)
	{
		String p[] = s.split(" ");
		int lines = 0;
		int buff = 0;
		for (int i = 0; i < p.length; i++)
		{
			int len = p[i].length();
			if (len % lineLen != 0)
				if (len + buff != lineLen)
					len++;
			if (len > lineLen)
			{
				if (i != 0)
					lines++;
				lines += len / lineLen;
				buff = len % lineLen;
			}
			else if (len + buff > lineLen)
			{
				buff = len;
				lines++;
			}
			else
				buff += len;
		}
		lines = buff > 0 ? lines + 1 : lines;
		return lines;
	}
	
	/*
	 * This method seeks the definition of a given word online by
	 * connecting to a website that has the definition in the 
	 * html, parsing it out, and returning it.
	 */
	
	public static String search (String toSearch) 
	{
		boolean found = true;
    	String def = "";
		try 
		{
			/*
			 *  I was originally using dictionary.com which html that is more consistantly
			 *  formatted. They have since blocked incoming requests of this type, (presumably)
			 *  due to high traffic volume from when I was testing this feature and the precursor
			 *  to the spell check in an automated way.
			 */
			
			// Establish connection and associated vars.
			
			URL url = new URL("https://www.merriam-webster.com/dictionary/" + toSearch);
	        URLConnection connection = url.openConnection();
	        connection.setConnectTimeout(5000);
	        BufferedReader html = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line = null;
	        int count = 0;
	        String defCatch = toSearch.toLowerCase() + " definition is - ";
	        
	        // Read though the html to find the definition (which is always before line 51);
	        
	        while ((line = html.readLine()) != null) 
	        {
	        	String s = line.toLowerCase();
	        	if (count == 51) { 
	        		found = false;
	        		break;
	        	}
	        	else if (s.contains(defCatch)) 
	        	{
	        		int start = s.indexOf(defCatch) + defCatch.length();
	        		def = (line.substring(start, line.length() - 2)).replaceAll(" See more.", "");
	        		break;
	        	}
	        }
	        html.close();
		}
		catch (Exception e)
		{ 
			return "[CONNECTION TIMED OUT]";
		}
		
		// return the definition, or lack there of.
		
		if (found) 
		{
			def = def.replaceAll("Synonym Discussion of "+toSearch.toLowerCase(), "");
			def = def.replaceAll("How to use "+toSearch.toLowerCase() + " in a sentence.", "");
			if (toSearch.toLowerCase().equals("auden"))
				def = "The person who wrote this program, Auden Childress. This may also refer to the " + def;
			return def;
		}
        else
	       return "[WORD NOT FOUND]";
	}
	
	private static int Compare (String o, String m)
	{
		if (o.length() > 28)
			return -1;
		o = o.toLowerCase();
		m = m.toLowerCase();
		char O[] = o.toCharArray();
		char M[] = m.toCharArray();
		if (O.length == M.length)
		{
			int subs = getSubs(O, M);
			int diff = getVal(O) - getVal(M);
			diff = diff < 0 ? -diff : diff;
			if (subs > 2 || (subs + diff/26 > O.length))
				return -1;
			diff = diff < 0 ? -diff : diff;
			if (subs == 2 && diff == 0)
				return 0;
			else if (subs == 1 && diff < 26)
				return 1;
			else if (diff < 52)
				return 2;
		}
		else
		{
			int diff = O.length - M.length;
			diff = diff < 0 ? -diff : diff;
			if (diff > 2 || M.length == 1)
				return -1;
			int diff1 = getVal(O) - getVal(M);
			diff1 = diff1 < 0 ? -diff1 : diff1;
			diff1 -= 96;
			int sim = getSim(O, M);
			if (O.length < 4)
			{
				if (m.substring(1).equals(o) || m.substring(0, M.length - 1).equals(o))
					return 0;
				else
					return -1;
			}
			if (diff == 1 && (diff1 < 26 && diff1 > -1) && sim == 1)
				return 0;
			else if (diff == 1 && sim == 1)
				return 1;
		}
		return -1;
	}
	
	private static int getSubs (char o[], char m[])
	{
		// cover substitution
		int subs = 0;
		for (int i = 0; i < o.length; i++)
		{
			if (o[i] != m[i])
			{
				subs++;
				if (subs > 2)
					break;
				else if (!isVowel(o[i]) || !isVowel(m[i]))
					subs++;
			}
		}
		return subs;
	}
	
	private static int getSim (char o[], char m[])
	{
		// covers insertion and deletion
		int flag = 0;
		for (int i = 0, j = 0; i < o.length && j < m.length; i++, j++)
		{
			if (o[i] != m[j])
			{
				if (flag != 0)
					return 0;
				flag++;
				if (o.length > m.length)
					i++;
				else
					j++;
			}
		}
		return 1;
	}
	
	private static String getRep (String s)
	{
		String outer[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
		String inner[] = {"", "h", "w"};
		s = s.toLowerCase();
		for (String o : outer)
			for (String i : inner)
				s = s.replaceAll(o+i+o, o);
		char c = s.charAt(0);
		String arr0[] = {"a","e", "i", "o","u","y","h","w"};
		for (String f : arr0)
			s = s.replaceAll(f, "");
		if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u')
			s = c+s;
		String arr1[] = {"b", "f", "p", "v"};
		String arr2[] = {"c", "g", "j", "k", "q", "s", "x", "z"};
		String arr3[] = {"d", "t"};
		String arr4[] = {"l"};
		String arr5[] = {"m", "n"};
		String arr6[] = {"r"};
		for (String r : arr1)
			s = s.replaceAll(r, "1");
		for (String r : arr2)
			s = s.replaceAll(r, "2");
		for (String r : arr3)
			s = s.replaceAll(r, "3");
		for (String r : arr4)
			s = s.replaceAll(r, "4");
		for (String r : arr5)
			s = s.replaceAll(r, "5");
		for (String r : arr6)
			s = s.replaceAll(r, "6");
		return s;
	}
	
	public static String autoCap (String t, int loc)
	{
		//TODO cap at /n/t
		Word temp = heads[0];
		int sec[] = findSec(t,loc);
		String w = t.substring(sec[0], sec[1]);
		String grammer[] = {" ","\\.",",",";","\\?",":","!"};
		for (String g : grammer)
			{
				w = w.replaceAll(" i"+g, " I"+g);
				w = w.replaceAll("\ti"+g, "\tI"+g); 
			}

		char arr[] = w.toCharArray();
		while (temp.nCap != null)
		{
			char wrd[] = temp.word.toCharArray();
			for (int i = 0; i + wrd.length < arr.length; i++)
			{
				if (arr[i] != ' ' && arr[i] != '\t')
					continue;
				int l = w.toLowerCase().indexOf(temp.word.toLowerCase());
				if (l != i+1)
					continue;
				try
				{
					if (isGrammer(arr[wrd.length+i+1]) == 1)
					{
						arr[i+1] = wrd[0];
						i+=wrd.length-1;
					}
				}
				catch (Exception e)
				{ }
			}
			temp = temp.nCap;
		}
		String ret = "";
		for (char c : arr)
			ret+=c;
		ret = t.substring(0, sec[0])+ret+t.substring(sec[1]);
		return ret;
	}
	
	private static int[] findSec (String t, int loc)
	{
		byte lGram = 0;
		byte rGram = 0;
		int arr[] = {loc, loc};
		for (int i = loc-1; i >= 0; i--)
		{
			arr[0] = i;
			if (isGrammer(t.charAt(i)) == 1)
				lGram++;
			if (lGram == 3)
				break;
		}
		for (int i = loc - 1 < 0 ? 0 : loc - 1; i < t.length(); i++)
		{
			arr[1] = i+1;
			if (isGrammer(t.charAt(i)) == 1)
				rGram++;
			if (rGram == 3)
				break;
		}
		return arr;
	}
}
