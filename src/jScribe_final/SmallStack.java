/** 
 * @author Auden Childress
 * @version final
 * @since March, 2018
 * Copyright 2019
 * GeistfulAutomaton@gmail.com
 */

package jScribe_final;

public class SmallStack
{
	/*
	 * This class is used to store the text and page associated with
	 * the undo and redo function.
	 */
	
	private byte size;
	private byte maxSize;
	private node mostRecent;
	private node current;
	
	private class node 
	{
		String pages[];
		int page;
		node next;
		node previous;
		public node (String pushedPages[], int pageNum) 
		{
			page = pageNum;
			pages = pushedPages;
		}
	}
	
	public void updateSize (byte numOfUndos)
	{
		maxSize = numOfUndos > 1 ? numOfUndos : 2;
	}
	
	public boolean isEmpty ()
	{
		return size > 1;
	}
	
	public boolean canRedo () 
	{
		return current.next != null;
	}
	
	public SmallStack (byte numOfUndos) 
	{
		size = 0;
		updateSize(numOfUndos);
		String pages[] = {" "};
		push(pages, 0);
	}
	
	public void push (String pages[], int page) 
	{
		if (size >= maxSize)
			removeLast();
		else if (mostRecent == null)
			mostRecent = current = new node (pages, page);
		else 
		{
			node temp = new node (pages, page);
			current.next = temp;
			temp.previous = current;
			mostRecent = current = temp;
		}
		size++;
	}
	
	public int getPg()
	{
		return current.page;
	}
	
	public String[] pop () 
	{
		String pages[] = current.pages;
		if (size > 1) 
		{
			current = current.previous;
			size--;
		}
		return pages;
	}
	
	public String[] redo ()
	{
		current = current.next;
		size++;
		if (current.next == null)
			return current.pages;
		return current.next.pages;
	}
	
	private void removeLast () 
	{
		node temp;
		for (temp = mostRecent; temp.previous != null; temp = temp.previous);
		temp = null;
		size--;
	}
}
