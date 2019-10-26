/*
 * The MIT License
 *
 * Copyright 2019 Nicholas Rodie.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package zmerge;

import java.awt.event.*;
import java.awt.Color;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.border.*;
import java.util.HashMap;
import com.garmin.fit.*;

/**
 *
 * @author Nicholas Rodie
 */
public class Zmerge extends JPanel implements ActionListener, FocusListener {  
    
    private static final String APP_NAME = "Zmerge";
    private static final String VERSION = "0.0.0";
    private static final long serialVersionUID = 2829528799561163825L;  
    
    private enum FIELD {
        ALTITUDE,
        DISTANCE,
        LATITUDE,
        LONGITUDE,
        SPEED
     };
    
    private enum ACTION {
        OPEN_FILE,
        SAVE_FILE
    };

    private JButton _gFileBtn, _zFileBtn, _mFileBtn, _mergeBtn, _resetBtn, _quitBtn;
    private JTextArea _gText, _zText, _mText;
    private String _gPath, _zPath, _mPath;
    private HashMap<Integer, HashMap<Enum, Double>> _records;
    
    @Override
    public void focusGained(FocusEvent e) {}

    @Override
    public void focusLost(FocusEvent e) {
        if(e.getSource() == _gText) {
            _gText.setCaretPosition(0);
        }
        else if(e.getSource() == _zText) {
            _zText.setCaretPosition(0);
        }
        else if(e.getSource() == _mText) {
            _mText.setCaretPosition(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == _gFileBtn) {
            String filePath = selectFile(ACTION.OPEN_FILE);
            if(filePath != null) {
                _gText.setText(filePath);
                _gText.setCaretPosition(0);
            }
        }
        else if(e.getSource() == _zFileBtn) {
            String filePath = selectFile(ACTION.OPEN_FILE);
            if(filePath != null) {
                _zText.setText(filePath);
                _zText.setCaretPosition(0);
            }
        }
        else if(e.getSource() == _mFileBtn) {
            String filePath = selectFile(ACTION.SAVE_FILE);
            if(filePath != null) {
                _mText.setText(filePath);
                _mText.setCaretPosition(0);
            }
        } 
        else if(e.getSource() == _mergeBtn) {
            merge();
        } 
        else if(e.getSource() == _resetBtn) {
            _gText.setText(null);
            _zText.setText(null);
            _mText.setText(null);
        } 
        else if(e.getSource() == _quitBtn) {
            System.exit(0);
        }
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, APP_NAME, JOptionPane.ERROR_MESSAGE);
        throw new RuntimeException(message);
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, APP_NAME, JOptionPane.WARNING_MESSAGE);
    }

    private void info(String message) {
        JOptionPane.showMessageDialog(this, message, APP_NAME, JOptionPane.INFORMATION_MESSAGE);
    }

    private Boolean select(String message) {        
        return JOptionPane.showConfirmDialog(this, message, APP_NAME, JOptionPane.YES_NO_OPTION) == 0;
    }
    
    private JScrollPane newPane(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setViewportBorder(new EmptyBorder(5,5,5,0));
        scrollPane.setBackground(Color.WHITE);
        return scrollPane;
    }

    private void createGui() {  
        
        _gFileBtn = new JButton("Select Garmin Fit File");
        _gFileBtn.addActionListener(this);
        _gText = new JTextArea();
        _gText.addFocusListener(this);
        JScrollPane gPane = newPane(_gText);

        _zFileBtn = new JButton("Select Zwift Fit File");
        _zFileBtn.addActionListener(this);
        _zText = new JTextArea();
        _zText.addFocusListener(this);
        JScrollPane zPane = newPane(_zText);
        
        _mFileBtn = new JButton("Save Merged File As");        
        _mFileBtn.addActionListener(this);        
        _mText = new JTextArea();
        _mText.addFocusListener(this);
        JScrollPane mPane = newPane(_mText);
         
        _resetBtn = new JButton("Reset");        
        _resetBtn.addActionListener(this);

        _mergeBtn = new JButton("Merge");        
        _mergeBtn.addActionListener(this);

        _quitBtn = new JButton("Quit");        
        _quitBtn.addActionListener(this);

        JFrame gui = new JFrame(APP_NAME + " " + VERSION);
        gui.setLayout(null);
        gui.add(_gFileBtn);
        gui.add(gPane);
        gui.add(_zFileBtn);
        gui.add(zPane);        
        gui.add(_mFileBtn);
        gui.add(mPane);        
        gui.add(_mergeBtn);
        gui.add(_resetBtn);
        gui.add(_quitBtn);
        
        int abw = 113;  // action button width
        int fbw = 160;  // file button width
        int bh = 27;    // button height  
        int px = 10;    // padding x
        int py = 16;    // padding y 		
        int bx = 15;    // 15 = window border x
        int by = 38;    // 38 = window border y 
        int ww = 640;   // window width

        _gFileBtn.setBounds(px, 1 * py + 0 * bh, fbw, bh);
        gPane.setBounds(2 * px + fbw, 1 * py + 0 * bh, ww - bx - fbw - 3 * px, bh);
        _zFileBtn.setBounds(px, 2 * py + 1 * bh, fbw, bh);
        zPane.setBounds(2 * px + fbw, 2 * py + 1 * bh, ww - bx - fbw - 3 * px, bh);
        _mFileBtn.setBounds(px, 3 * py + 2 * bh, fbw, bh);
        mPane.setBounds(2 * px + fbw, 3 * py + 2 * bh, ww - bx - fbw - 3 * px, bh);
        _mergeBtn.setBounds(ww - 3 * (abw + px) - bx - 1, 4 * py + 3 * bh, abw, bh);
        _resetBtn.setBounds(ww - 2 * (abw + px) - bx - 1, 4 * py + 3 * bh, abw, bh);
        _quitBtn.setBounds(ww - abw - px - bx - 1, 4 * py + 3 * bh, abw, bh);
        gui.setSize(ww, by + 5 * py + 4 * bh);
        
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setLocationRelativeTo(null); 
        gui.setVisible(true);
    }    

    private void checkInStream(String path) {        
        InputStream stream;
        Decode decode;
        try {            
            validatePath(path);            
            stream = new FileInputStream(path);
            decode = new Decode();
            if(!decode.checkFileIntegrity((InputStream) stream)) 
                warn("\"" + path + "\"\nFIT file integrity check failed. Continuing...");
            stream.close();            
        }
        catch (FitRuntimeException | IOException e) {
            error("\"" + path + "\"\nError reading file.\n" + e.getMessage());  
            throw new RuntimeException();
        }
    } 
    
    private void checkOutStream(String path) {        
        File file;
        FileEncoder encoder;
        FileIdMesg fileIdMesg;
        try {            
            validatePath(path);
            file = new File(path);
            if(file.exists() && !select("\"" + path + "\"\nThe merge file already exists. Do you want to overwrite?")) 
                throw new RuntimeException(); 
            encoder = new FileEncoder(new File(path), Fit.ProtocolVersion.V2_0);
            fileIdMesg = new FileIdMesg();
            encoder.write(fileIdMesg);
            encoder.close();
        }
        catch (RuntimeException e) {
            error("\"" + path + "\"\nUnable to write file.\n" + e.getMessage());  
            throw new RuntimeException();
        }			
    }

    private String selectFile(ACTION action) {
        JFileChooser fc = new JFileChooser();  
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "FIT Files (*.fit)";
            }         
            @Override
            public boolean accept(File file) {
                if(file.isDirectory()) return true;
                else return file.getName().toLowerCase().endsWith(".fit");
            }
        });    
        if(action == ACTION.OPEN_FILE && fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getPath();
        }
        else if(action == ACTION.SAVE_FILE && fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getPath();
            if(path.toLowerCase().endsWith(".fit")) return path;
            else return path + ".fit";
        }    
        else return null;
    }    

    private void validatePath(String path) {
        if(path.isEmpty()) {
            info("You must set the file paths.");
            throw new RuntimeException();
        }
        else if(!path.toLowerCase().endsWith(".fit")) {
            info("\"" + path + "\"\nFilenames must end with .fit extension.");
            throw new RuntimeException();
        }
    }    

    private void getZwiftData() {
        class Listener implements MesgListener {            
            
            @Override
            public void onMesg(Mesg mesg) { 

                int type = mesg.getNum(); 
                switch (type) {
                    
                    case MesgNum.RECORD:
                    
                        // make sure not to read any records that don't have required data
                        Double altitude = mesg.getFieldDoubleValue(RecordMesg.AltitudeFieldNum);
                        if(altitude == null) return;
                        Double distance = mesg.getFieldDoubleValue(RecordMesg.DistanceFieldNum);
                        if(distance == null) return;
                        Double latitude = mesg.getFieldDoubleValue(RecordMesg.PositionLatFieldNum);
                        if(latitude == null) return;
                        Double longitude = mesg.getFieldDoubleValue(RecordMesg.PositionLongFieldNum);
                        if(longitude == null) return;
                        Double speed = mesg.getFieldDoubleValue(RecordMesg.SpeedFieldNum);
                        if(speed == null) return;

                        // save selected fields for each timestamp
                        Integer timestamp = mesg.getFieldIntegerValue(RecordMesg.TimestampFieldNum);
                        HashMap<Enum, Double> zData = new HashMap<>();  
                        _records.put(timestamp, zData);
                        zData.put(FIELD.ALTITUDE, altitude);
                        zData.put(FIELD.DISTANCE, distance);            
                        zData.put(FIELD.LATITUDE, latitude);
                        zData.put(FIELD.LONGITUDE, longitude);
                        zData.put(FIELD.SPEED, speed);   
                        break;
                    
                    case MesgNum.FILE_ID:

                        Integer man = mesg.getFieldIntegerValue(FileIdMesg.ManufacturerFieldNum);
                        if(man != null && man != Manufacturer.ZWIFT)
                            throw new FitRuntimeException("Manufacturer does not match Zwift."); 
                        break;

                    default:
                        break;
                }
            }
        }

        _records = new HashMap<>();
        InputStream stream; 
        Decode decode = new Decode();
        Listener listener = new Listener();
        decode.addListener((MesgListener) listener);   
        
        try {                      
            stream = new FileInputStream(_zPath);            
            decode.read(stream);
            stream.close(); 
        } 
        catch (FitRuntimeException | IOException e) {
            error("Error reading Zwift file.\n" + e.getMessage());            
            throw new RuntimeException();
        }
    }
    
    private void createNewGarminFile() {
        class Listener implements MesgListener, MesgDefinitionListener {
            final private int VIRTUAL_ACTIVITY = SubSport.VIRTUAL_ACTIVITY.getValue();
            FileEncoder _encode;
            Double _ascent, _descent, _lapAscent, _lapDescent, _lastAlt, _totDist, _lapDist, _maxSpd, _maxLapSpd, _offset;
            Integer _lapTime, _lastTime, _startTime;            

            public Listener() {
                _encode = new FileEncoder(new File(_mPath), Fit.ProtocolVersion.V2_0);
                _offset = null;
                _ascent = _descent = _lapAscent = _lapDescent = _lastAlt = _totDist = _lapDist = _maxSpd = _maxLapSpd = 0.0;
                _lapTime = _lastTime = _startTime = 0;
            }

            @Override
            public void onMesg(Mesg mesg) { 
                
                int type = mesg.getNum(); 
                if(!MesgNum.getStringFromValue(type).equals("")) {
                    
                    switch (type) {
                        
                        case MesgNum.RECORD:
                            
                            // ignore records that dont match a Zwift timestamp
                            Integer timestamp = mesg.getFieldIntegerValue(RecordMesg.TimestampFieldNum);
                            if(_records.get(timestamp) == null) return;
                            HashMap<Enum, Double> zData = _records.get(timestamp);
                            if(zData == null) return;
                            else if(_startTime == 0) {
                                _startTime = timestamp;
                                _lapTime = timestamp;
                                _lastAlt = zData.get(FIELD.ALTITUDE);
                            }   

                            // calculate altitudes
                            Double altitude = zData.get(FIELD.ALTITUDE);
                            if(altitude > _lastAlt) {
                                _ascent += altitude - _lastAlt;
                                _lapAscent += altitude - _lastAlt;
                            }
                            else if(altitude < _lastAlt) {
                                _descent += _lastAlt - altitude;
                                _lapDescent += _lastAlt - altitude;
                            }   
                            
                            // distance offset removes any Zwift distance not covered by Garmin timestamps
                            if(_offset == null) _offset = zData.get(FIELD.DISTANCE);
                            _totDist = zData.get(FIELD.DISTANCE) - _offset;
                            
                            // watch for max speeds
                            Double speed = zData.get(FIELD.SPEED);
                            if(speed > _maxSpd) _maxSpd = speed;
                            if(speed > _maxLapSpd) _maxLapSpd = speed;
                            
                            // update the selected Garmin fields
                            // elevation
                            mesg.setFieldValue(RecordMesg.AltitudeFieldNum, altitude);
                            mesg.setFieldValue(RecordMesg.EnhancedAltitudeFieldNum, altitude);
                            // distance
                            mesg.setFieldValue(RecordMesg.DistanceFieldNum, _totDist);
                            // location
                            mesg.setFieldValue(RecordMesg.PositionLatFieldNum, zData.get(FIELD.LATITUDE));
                            mesg.setFieldValue(RecordMesg.PositionLongFieldNum, zData.get(FIELD.LONGITUDE));
                            // speed
                            mesg.setFieldValue(RecordMesg.SpeedFieldNum, speed);
                            mesg.setFieldValue(RecordMesg.EnhancedSpeedFieldNum, speed);
                            
                            _lastAlt = altitude;
                            _lastTime = timestamp;
                            break;
                            
                        case MesgNum.LAP:
                            
                            // calculate lap values
                            int lapTime = _lastTime - _lapTime;
                            Double lapDist = _totDist - _lapDist;
                            
                            // update the selected Garmin fields
                            // elevation
                            mesg.setFieldValue(LapMesg.TotalAscentFieldNum, _lapAscent);
                            mesg.setFieldValue(LapMesg.TotalDescentFieldNum, _lapDescent);
                            // distance
                            mesg.setFieldValue(LapMesg.TotalDistanceFieldNum, lapDist);
                            // speed
                            mesg.setFieldValue(LapMesg.AvgSpeedFieldNum, lapDist / lapTime);
                            mesg.setFieldValue(LapMesg.MaxSpeedFieldNum, _maxLapSpd);
                            mesg.setFieldValue(LapMesg.EnhancedAvgSpeedFieldNum, lapDist / lapTime);
                            mesg.setFieldValue(LapMesg.EnhancedMaxSpeedFieldNum, _maxLapSpd);                            
                            // time
                            mesg.setFieldValue(LapMesg.TotalElapsedTimeFieldNum, lapTime);
                            mesg.setFieldValue(LapMesg.TotalTimerTimeFieldNum, lapTime);
                            // set virtual
                            mesg.setFieldValue(LapMesg.SubSportFieldNum, VIRTUAL_ACTIVITY);
                            
                            _lapAscent = _lapDescent = _maxLapSpd = 0.0;
                            _lapTime = _lastTime;
                            _lapDist = _totDist;
                            break;
                            
                        case MesgNum.SESSION:
                            
                            int elapsedTime = _lastTime - _startTime;
                            // update the selected Garmin fields
                            // elevation
                            mesg.setFieldValue(SessionMesg.TotalAscentFieldNum, _ascent);
                            mesg.setFieldValue(SessionMesg.TotalDescentFieldNum, _descent);
                            // speed
                            mesg.setFieldValue(SessionMesg.AvgSpeedFieldNum, _totDist / elapsedTime);
                            mesg.setFieldValue(SessionMesg.MaxSpeedFieldNum, _maxSpd);
                            mesg.setFieldValue(SessionMesg.EnhancedAvgSpeedFieldNum, _totDist / elapsedTime);
                            mesg.setFieldValue(SessionMesg.EnhancedMaxSpeedFieldNum, _maxSpd);
                            // distance
                            mesg.setFieldValue(SessionMesg.TotalDistanceFieldNum, _totDist);
                            // time
                            mesg.setFieldValue(SessionMesg.TotalElapsedTimeFieldNum, elapsedTime);
                            mesg.setFieldValue(SessionMesg.TotalTimerTimeFieldNum, elapsedTime);
                            //set virtual
                            mesg.setFieldValue(SessionMesg.SubSportFieldNum, VIRTUAL_ACTIVITY);
                            break;
                            
                        case MesgNum.FILE_ID:
                            
                            Integer man = mesg.getFieldIntegerValue(FileIdMesg.ManufacturerFieldNum);
                            if(man != null && man == Manufacturer.GARMIN) _encode.write(mesg);
                            else throw new FitRuntimeException("Manufacturer does not match Garmin.");
                            break;
                            
                        default:
                            break;                     
                    }
                     _encode.write(mesg);  
                }
            }

            @Override
            public void onMesgDefinition(MesgDefinition mesg) {
                if(!MesgNum.getStringFromValue(mesg.getNum()).equals("")) _encode.write(mesg);
            }

            private void close() {
                _encode.close();
            }
        }

        InputStream stream; 
        Decode decode = new Decode();
        Listener listener = new Listener();
        decode.addListener((MesgListener) listener);
        decode.addListener((MesgDefinitionListener) listener);    
            
        try {                     
            stream = new FileInputStream(_gPath);  
            decode.read(stream);
            listener.close();              
            stream.close();                      
        } 
        catch (FitRuntimeException | IOException e) {     
            error("Error reading Garmin file.\n" + e.getMessage());            
            throw new RuntimeException();
        }
    }

    public void merge() {

        _gPath = _gText.getText();
        _zPath = _zText.getText();
        _mPath = _mText.getText();
                
        try {
            if(_mPath.equals(_gPath) || _mPath.equals(_zPath)) {
                error("You are trying to overwrite an input file!");
                throw new RuntimeException();
            }
            checkInStream(_gPath);
            checkInStream(_zPath);
            checkOutStream(_mPath);            
            getZwiftData();
            createNewGarminFile();
            
            info("Merging process completed successfully.");
        }
        catch (RuntimeException e) {
            warn("Merging process did not complete.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Zmerge app = new Zmerge();
            app.createGui();
        });
    }
}