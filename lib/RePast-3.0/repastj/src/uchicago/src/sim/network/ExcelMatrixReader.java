/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.sim.network;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import uchicago.src.sim.util.SimUtilities;

public class ExcelMatrixReader implements NetworkMatrixReader{

	private HSSFWorkbook workbook = null;
	private int type = NetworkConstants.LARGE;
	private int curRow = 0;
	private int numRows = 0;

	public ExcelMatrixReader(String filename) {
		FileInputStream in = null;
		try{
			in = new FileInputStream(filename);
			POIFSFileSystem fs = new POIFSFileSystem(in);
			workbook = new HSSFWorkbook(fs);
			in.close();
		}catch(IOException ex){
			try{
				if(in != null) in.close();
			}catch(IOException ex1){}
			SimUtilities.showError("Error reading network file: " + filename, ex);
			System.exit(0);
		}
	}

	private AdjacencyMatrix getLabeledMatrix(HSSFSheet sheet) throws IOException{
		AdjacencyMatrix m = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		row = sheet.getRow(0);
		Vector v = new Vector(row.getPhysicalNumberOfCells() - 1);
		Iterator ci = row.cellIterator();
		while(ci.hasNext()){
			cell = (HSSFCell) ci.next();
			if(cell.getCellNum() == 0){
				if(!(cell.getStringCellValue().equals("")))
	        throw new IOException("Badly formatted Excel matrix file: " +
                            "labels must start at 1, 2");
			}
//			v.set(cell.getCellNum() - 1, cell.getStringCellValue());
		}
		if(!(v.size() == 0))
			m = AdjacencyMatrixFactory.createAdjacencyMatrix(v, type);
		return m;
	}


	private AdjacencyMatrix getNonLabeledMatrix(HSSFSheet sheet){
		AdjacencyMatrix m = null;
		int height = sheet.getPhysicalNumberOfRows() - 1;
		m = AdjacencyMatrixFactory.createAdjacencyMatrix(height, height, type);
		return m;
	}

	private AdjacencyMatrix getMatrix(String sheetName) throws IOException{
		HSSFSheet sheet = workbook.getSheet(sheetName);
		HSSFRow row = null;
		HSSFCell cell = null;
		AdjacencyMatrix m = null;
		m = getLabeledMatrix(sheet);
		numRows = sheet.getPhysicalNumberOfRows();
		if(m == null){
			m = getNonLabeledMatrix(sheet);
		}
		curRow++;
		for(int i = 1; i < numRows ; i++){
			row = sheet.getRow(i);
			Iterator ci = row.cellIterator();
			//String printRow = "Row " + i;
			//int numCells = row.getPhysicalNumberOfCells();
			while(ci.hasNext()){
				cell = (HSSFCell) ci.next();
				Short pos = new Short(cell.getCellNum());
				m.set(((int)pos.intValue() - 1), i - 1, cell.getNumericCellValue());
			}
		}
		if(!sheetName.startsWith("Sheet"))
			m.setMatrixLabel(sheetName);
		return m;
	}

	public Vector getMatrices() throws IOException{
		return getMatrices(NetworkConstants.LARGE);
	}

	public Vector getMatrices(int matrixType)throws IOException{
		Vector v = new Vector();
		type = matrixType;
		int numSheets = workbook.getNumberOfSheets();
		for(int i = 0 ; i < numSheets ; i++){
			v.add(getMatrix(workbook.getSheetName(i)));
		}
		return v;	
	}

	public static void main(String[] args){
		try{
			List nodeList = NetworkFactory.getNetwork("uchicago/src/sim/test/Exported.xls", 
					NetworkFactory.EXCEL, 
					DefaultNode.class, 
					DefaultEdge.class,
					NetworkConstants.LARGE);

			System.out.println(nodeList.size());
			//ex.getMatrices();
			System.exit(0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void close(){
		workbook = null;
	}
}
