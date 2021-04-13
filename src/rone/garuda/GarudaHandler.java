/*
 * RONE
 * Copyright (C) [2021] [Carlin. R. Connell]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rone.garuda;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import jp.sbi.garuda.backend.GarudaBackend;
import jp.sbi.garuda.backend.POJOs.CompatibleGadgetDetails;
import jp.sbi.garuda.backend.exception.NoFileToSendException;
import jp.sbi.garuda.backend.incomingHandler.IncomingRequestProtocolHandler;
import jp.sbi.garuda.backend.incomingHandler.garudaActionListeners.requests.LoadDataRequestActionListener;
import jp.sbi.garuda.backend.incomingHandler.garudaActionListeners.responses.ActivateGadgetResponseActionListener;
import jp.sbi.garuda.backend.incomingHandler.garudaActionListeners.responses.GetCompatibleGadgetListResponseActionListener;
import jp.sbi.garuda.backend.incomingHandler.responseCodes.GarudaResponseCode;
import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import jp.sbi.garuda.backend.plugin.pipeline.GarudaBackendPipelinePlugin;
import jp.sbi.garuda.backend.plugin.pipeline.exceptions.PipelineNotInitializedException;
import jp.sbi.garuda.backend.plugin.pipeline.responseCodes.GarudaPipelineResponseCode;
import jp.sbi.garuda.backend.ui.GarudaGlassPanel;
import rone.filemanager.FileManager;
import rone.ui.MainWindow;

public class GarudaHandler {

	private GarudaBackend mGarudaBackend;
	private JFrame mParentFrame;
	private GarudaBackendPipelinePlugin mPipelinePlugin;
	private MainWindow mMainWindow;
	
	public GarudaHandler(MainWindow mainWindow) throws GarudaConnectionNotInitializedException, NetworkConnectionException
	{
		this.mMainWindow = mainWindow;
		this.mParentFrame = mMainWindow.getFrame();
		
		mGarudaBackend = new GarudaBackend(GarudaConstants.GARUDA_ID, GarudaConstants.GARUDA_NAME, mParentFrame);
		
		mGarudaBackend.addGarudaGlassPanel(mParentFrame, null);
		
		initGarudaListeners();
		initPipeline();
		
		mGarudaBackend.activateGadget();
	
	}

	private void initPipeline () 
	{
		mPipelinePlugin = new GarudaBackendPipelinePlugin() ;
		
		mGarudaBackend.registerProtocolHandlerPlugin(mPipelinePlugin);
		
	}
	
	public boolean isConnected() {
		return mGarudaBackend.isConnectionLive();
	}
	
	public void activateConnection() 
	{
		mGarudaBackend.activateGadget();
	}
	
	public GarudaGlassPanel getGarudaGlassPanel () 
	{
		return mGarudaBackend.getGarudaGlassPanel() ;
	}
	
	public IncomingRequestProtocolHandler accessIncomingRequestListenerHandler (  )
	{
		return mGarudaBackend.getIncomingRequestHandler() ;
	}
	
	public IncomingRequestProtocolHandler accessIncomingResponseListenerHandler (  )
	{
		return mGarudaBackend.getIncomingRequestHandler() ;
	}
	
	public GarudaBackendPipelinePlugin accessPipelinePlugin () 
	{
		return mPipelinePlugin;
	}
	
	public void garudaDiscover(File fileToDiscover, String fileFormat)
	{
		if ( fileToDiscover == null || !fileToDiscover.exists())
			throw new IllegalArgumentException("File does not exist");
		if ( fileFormat == null || fileFormat.equals(""))
			throw new IllegalArgumentException("Illegal file format.");
		
		
		mGarudaBackend.getCompatibleGadgetList(fileToDiscover, fileFormat);
	}
	
	public void garudaDiscover(DiscoveryTouple discoveryTouple)
	{
		if ( discoveryTouple == null || discoveryTouple.getFileFormat() == null || discoveryTouple.getFileToDiscover()== null)
			throw new IllegalArgumentException( "Incompatible discovery touple") ;
		
		mGarudaBackend.getCompatibleGadgetList(discoveryTouple.getFileToDiscover(), discoveryTouple.getFileFormat());
	}
	
	public void sendFileTo(CompatibleGadgetDetails targetGadget, boolean isStream) throws NoFileToSendException
	{
		if (!isStream)
			mGarudaBackend.sendDataToGadgetAsFile(targetGadget);
		else
			mGarudaBackend.sendDataToGadgetAsStream(targetGadget);
	}
	
	public void sendPipelineReply (File pipelineFile, String fileFormat) throws JsonGenerationException, JsonMappingException, IOException, PipelineNotInitializedException
	{
		mPipelinePlugin.sendSendPipelineMessageToGadgetResponse(pipelineFile, fileFormat);
	}
	
	public void sendPipelineOperationFailed (GarudaPipelineResponseCode pipelineResponseCode ) throws JsonGenerationException, JsonMappingException, IOException, PipelineNotInitializedException
	{
			mPipelinePlugin.sendPipelineFailedToGadgetResponse(pipelineResponseCode);
	}
	
	private boolean isNull(Object o){
		return o == null;
	}
	
	private String[] getColumnHeaders(String columnHeaders, String seperator) {
		int len = columnHeaders.length();
		columnHeaders.subSequence(1, len-2);
		return columnHeaders.split(",");
	}
	
	private boolean hasProperties(String senderName) {
		Properties properties = FileManager.getProperties();
		String propColumnHeaders = (String)properties.get("Garuda." + senderName + ".column_headers");
		String propSeperator = (String)properties.get("Garuda." + senderName + ".seperator");
		String propSkipHeader = (String)properties.get("Garuda." + senderName + ".skip_header");
		return !isNull(propColumnHeaders) && !isNull(propSeperator) && !isNull(propSkipHeader);
	}
	
	
	
	private void initGarudaListeners () {
				
		mGarudaBackend.getIncomingRequestHandler().addLoadDataRequestActionListener(new LoadDataRequestActionListener() {

			@Override
			public void loadDataRequestReceivedAsFile(
					File file, 
					String senderId, 
					String senderName,
					String originDeviceId, 
					String currentDeviceId, 
					String fileName, 
					String fileFormat) {
				
				if(!file.exists())
					return;
				
				boolean hasSenderId = hasProperties(senderId);
				boolean hasName = hasProperties(senderName);
				
				boolean hasProperties = hasSenderId || hasName;
				ArrayList<Object[]> loadedFile = null; 
				String[] columnHeaders = null;
				String tableName = senderName + " (" + fileFormat + ")"; 
				try {
					
					if(hasProperties) {
						String key = hasSenderId ? senderId : senderName;
						
						Properties properties = FileManager.getProperties();
						String propColumnHeaders = (String)properties.get("Garuda." + key + ".column_headers");
						String propSeperator = (String)properties.get("Garuda." + key + ".seperator");
						String propSkipHeader = (String)properties.get("Garuda." + key + ".skip_header");
						
	
						columnHeaders = getColumnHeaders(propColumnHeaders, propSeperator);
						boolean skipHeader = Boolean.getBoolean(propSkipHeader);
						
						loadedFile = FileManager.loadStructuredFile(file, propSeperator, skipHeader);
						
						
					} else {
						Object[] options = {"Continue","Cancel"};
	
						int n = JOptionPane.showOptionDialog(null,
								"File Name: " + fileName + "\n" + 
								"File Format: " + fileFormat + "\n" + 
								"Sender Name: " + senderName + "\n" + 
								"Sender ID: " + senderId + "\n",
						    "Garuda Import",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[0]);
						
						if(n == 1)
							return;
	
						String path = file.getAbsolutePath();
						String extension = FilenameUtils.getExtension(path);
						
						switch(extension) {
						case "csv": 
							columnHeaders = null;
							loadedFile = FileManager.loadCSV(file,  false);
							break; 
						
						case "txt": 
							columnHeaders = new String[1];
							columnHeaders[0] = fileFormat;
							loadedFile = FileManager.loadTextFile(file, true);
							break; 
						
						default:
							columnHeaders = new String[1];
							columnHeaders[0] = senderName;
							loadedFile = FileManager.loadTextFile(file, true);
							break; 
						}
						
					}
					
					if(isNull(loadedFile) || isNull(columnHeaders)) {
						JOptionPane.showMessageDialog(mParentFrame, "Failed to accept import data from the Garuda Platform.", "Import Error", JOptionPane.ERROR_MESSAGE);
					} else {
						mMainWindow.getTabbedPane().addTab(tableName, columnHeaders, null, loadedFile);
					}
				
				} catch (SQLException e) {
					MainWindow.showError(e);
				} catch (IOException e) {
					MainWindow.showError(e);
				}
			
			}

			@Override
			public void loadDataRequestReceivedAsStream(byte[] receivedData, String senderId, String senderName,
					String originDeviceId, String currentDeviceId, String fileName, String fileFormat) {

			}
		});
		
		mGarudaBackend.getIncomingResponseHandler().addActivateGadgetResponseActionListener(new ActivateGadgetResponseActionListener() {
			
			@Override
			public void gadgetActivationFailed(GarudaResponseCode response) {
				JOptionPane.showMessageDialog(mParentFrame, "Activation Failed " + response.toString(), "Garuda Error", JOptionPane.ERROR_MESSAGE);

			}
			
			@Override
			public void gadgetActivated() {
				//JOptionPane.showMessageDialog(mParentFrame, "Connected to Garuda ", "Garuda connection", JOptionPane.INFORMATION_MESSAGE);
			}
		}) ;
		
		mGarudaBackend.getIncomingResponseHandler().addGetCompatibleGadgetListResponseActionListener(new GetCompatibleGadgetListResponseActionListener() {
			
			@Override
			public void noCompatibleGadgetsFound(GarudaResponseCode responseCode) {
				JOptionPane.showMessageDialog(mParentFrame, "No compatible Gadgets " + responseCode.id, "Garuda Error", JOptionPane.ERROR_MESSAGE);
				
			}
			
			@Override
			public void gotErrorOnCompatibleGadgetListRequest(GarudaResponseCode responseCode) {
				JOptionPane.showMessageDialog(mParentFrame, "Error while getting compatible gadgets " + responseCode.id, "Garuda Error", JOptionPane.ERROR_MESSAGE);
			}
			
			@Override
			public void gotCompatibleGadgetList(List<CompatibleGadgetDetails> gadgetList) {
				
				getGarudaGlassPanel().showPanel(gadgetList);
				
			}
			
			@Override
			public void fileNotInOutBoundList(GarudaResponseCode responseCode) {
				JOptionPane.showMessageDialog(mParentFrame, "File not in output list " + responseCode.id, "Garuda Error", JOptionPane.ERROR_MESSAGE);
			}
		}) ;
	
	}
	
}
