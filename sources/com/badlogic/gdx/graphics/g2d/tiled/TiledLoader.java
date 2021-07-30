package com.badlogic.gdx.graphics.g2d.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

public class TiledLoader {
    public static TiledMap createMap(String tmxData) {
        return createMap((FileHandle) null, tmxData);
    }

    public static TiledMap createMap(FileHandle tmxFile) {
        return createMap(tmxFile, (String) null);
    }

    private static TiledMap createMap(FileHandle tmxFile, String tmxData) {
        final TiledMap map = new TiledMap();
        map.tmxFile = tmxFile;
        try {
            XmlReader xmlReader = new XmlReader() {
                boolean awaitingData = false;
                int col;
                String compression;
                Stack<String> currBranch = new Stack<>();
                TiledLayer currLayer;
                int currLayerHeight = 0;
                int currLayerWidth = 0;
                TiledObject currObject;
                TiledObjectGroup currObjectGroup;
                Property currProperty;
                int currTile;
                TileSet currTileSet;
                byte[] data;
                int dataCounter = 0;
                String dataString;
                String encoding;
                Polyline polygon;
                Polyline polyline;
                int row;

                /* renamed from: com.badlogic.gdx.graphics.g2d.tiled.TiledLoader$1$Polyline */
                class Polyline {
                    String name;
                    String points;

                    public Polyline(String name2) {
                        this.name = name2;
                    }

                    public Polyline() {
                    }
                }

                /* renamed from: com.badlogic.gdx.graphics.g2d.tiled.TiledLoader$1$Property */
                class Property {
                    String name;
                    String parentType;
                    String value;

                    Property() {
                    }
                }

                /* access modifiers changed from: protected */
                public void open(String name) {
                    this.currBranch.push(name);
                    if ("layer".equals(name)) {
                        this.currLayer = new TiledLayer();
                    } else if ("tileset".equals(name)) {
                        this.currTileSet = new TileSet();
                    } else if ("data".equals(name)) {
                        this.dataString = "";
                        this.awaitingData = true;
                    } else if ("objectgroup".equals(name)) {
                        this.currObjectGroup = new TiledObjectGroup();
                    } else if ("object".equals(name)) {
                        this.currObject = new TiledObject();
                    } else if ("property".equals(name)) {
                        this.currProperty = new Property();
                        this.currProperty.parentType = (String) this.currBranch.get(this.currBranch.size() - 3);
                    } else if ("polyline".equals(name)) {
                        this.polyline = new Polyline("polyline");
                    } else if ("polygon".equals(name)) {
                        this.polygon = new Polyline("polygon");
                    }
                }

                /* access modifiers changed from: protected */
                public void attribute(String name, String value) {
                    String element = this.currBranch.peek();
                    if ("layer".equals(element)) {
                        if ("width".equals(name)) {
                            this.currLayerWidth = Integer.parseInt(value);
                        } else if ("height".equals(name)) {
                            this.currLayerHeight = Integer.parseInt(value);
                        }
                        if (!(this.currLayerWidth == 0 || this.currLayerHeight == 0)) {
                            this.currLayer.tiles = (int[][]) Array.newInstance(Integer.TYPE, new int[]{this.currLayerHeight, this.currLayerWidth});
                        }
                        if ("name".equals(name)) {
                            this.currLayer.name = value;
                        }
                    } else if ("tileset".equals(element)) {
                        if ("firstgid".equals(name)) {
                            this.currTileSet.firstgid = Integer.parseInt(value);
                        } else if ("tilewidth".equals(name)) {
                            this.currTileSet.tileWidth = Integer.parseInt(value);
                        } else if ("tileheight".equals(name)) {
                            this.currTileSet.tileHeight = Integer.parseInt(value);
                        } else if ("name".equals(name)) {
                            this.currTileSet.name = value;
                        } else if ("spacing".equals(name)) {
                            this.currTileSet.spacing = Integer.parseInt(value);
                        } else if ("margin".equals(name)) {
                            this.currTileSet.margin = Integer.parseInt(value);
                        }
                    } else if ("image".equals(element)) {
                        if ("source".equals(name)) {
                            this.currTileSet.imageName = value;
                        }
                    } else if ("data".equals(element)) {
                        if ("encoding".equals(name)) {
                            this.encoding = value;
                        } else if ("compression".equals(name)) {
                            this.compression = value;
                        }
                    } else if ("objectgroup".equals(element)) {
                        if ("name".equals(name)) {
                            this.currObjectGroup.name = value;
                        } else if ("height".equals(name)) {
                            this.currObjectGroup.height = Integer.parseInt(value);
                        } else if ("width".equals(name)) {
                            this.currObjectGroup.width = Integer.parseInt(value);
                        }
                    } else if ("object".equals(element)) {
                        if ("name".equals(name)) {
                            this.currObject.name = value;
                        } else if ("type".equals(name)) {
                            this.currObject.type = value;
                        } else if ("x".equals(name)) {
                            this.currObject.f111x = Integer.parseInt(value);
                        } else if ("y".equals(name)) {
                            this.currObject.f112y = Integer.parseInt(value);
                        } else if ("width".equals(name)) {
                            this.currObject.width = Integer.parseInt(value);
                        } else if ("height".equals(name)) {
                            this.currObject.height = Integer.parseInt(value);
                        } else if ("gid".equals(name)) {
                            this.currObject.gid = Integer.parseInt(value);
                        }
                    } else if ("map".equals(element)) {
                        if ("orientation".equals(name)) {
                            map.orientation = value;
                        } else if ("width".equals(name)) {
                            map.width = Integer.parseInt(value);
                        } else if ("height".equals(name)) {
                            map.height = Integer.parseInt(value);
                        } else if ("tilewidth".equals(name)) {
                            map.tileWidth = Integer.parseInt(value);
                        } else if ("tileheight".equals(name)) {
                            map.tileHeight = Integer.parseInt(value);
                        }
                    } else if ("tile".equals(element)) {
                        if (this.awaitingData) {
                            if ("gid".equals(name)) {
                                this.col = this.dataCounter % this.currLayerWidth;
                                this.row = this.dataCounter / this.currLayerWidth;
                                if (this.row < this.currLayerHeight) {
                                    this.currLayer.tiles[this.row][this.col] = Integer.parseInt(value);
                                } else {
                                    Gdx.app.log("TiledLoader", "Warning: extra XML gid values ignored! Your map is likely corrupt!");
                                }
                                this.dataCounter++;
                            }
                        } else if ("id".equals(name)) {
                            this.currTile = Integer.parseInt(value);
                        }
                    } else if ("property".equals(element)) {
                        if ("name".equals(name)) {
                            this.currProperty.name = value;
                        } else if ("value".equals(name)) {
                            this.currProperty.value = value;
                        }
                    } else if ("polyline".equals(element)) {
                        if ("points".equals(name)) {
                            this.polyline.points = value;
                        }
                    } else if ("polygon".equals(element) && "points".equals(name)) {
                        this.polygon.points = value;
                    }
                }

                /* access modifiers changed from: protected */
                public void text(String text) {
                    if (this.awaitingData) {
                        this.dataString = this.dataString.concat(text);
                    }
                }

                /* access modifiers changed from: protected */
                public void close() {
                    String element = this.currBranch.pop();
                    if ("layer".equals(element)) {
                        map.layers.add(this.currLayer);
                        this.currLayer = null;
                    } else if ("tileset".equals(element)) {
                        map.tileSets.add(this.currTileSet);
                        this.currTileSet = null;
                    } else if ("object".equals(element)) {
                        this.currObjectGroup.objects.add(this.currObject);
                        this.currObject = null;
                    } else if ("objectgroup".equals(element)) {
                        map.objectGroups.add(this.currObjectGroup);
                        this.currObjectGroup = null;
                    } else if ("property".equals(element)) {
                        putProperty(this.currProperty);
                        this.currProperty = null;
                    } else if ("polyline".equals(element)) {
                        putPolyLine(this.polyline);
                        this.polyline = null;
                    } else if ("polygon".equals(element)) {
                        putPolyLine(this.polygon);
                        this.polygon = null;
                    } else if ("data".equals(element)) {
                        if ("base64".equals(this.encoding)) {
                            if (!(this.dataString == null) && !"".equals(this.dataString.trim())) {
                                this.data = Base64Coder.decode(this.dataString.trim());
                                if ("gzip".equals(this.compression)) {
                                    unGZip();
                                } else if ("zlib".equals(this.compression)) {
                                    unZlib();
                                } else if (this.compression == null) {
                                    arrangeData();
                                }
                            } else {
                                return;
                            }
                        } else if ("csv".equals(this.encoding) && this.compression == null) {
                            fromCSV();
                        } else if (this.encoding == null && this.compression == null) {
                            this.dataCounter = 0;
                        } else {
                            throw new GdxRuntimeException("Unsupported encoding and/or compression format");
                        }
                        this.awaitingData = false;
                    } else if ("property".equals(element)) {
                        putProperty(this.currProperty);
                        this.currProperty = null;
                    }
                }

                private void putPolyLine(Polyline polyLine) {
                    if (polyLine != null) {
                        if ("polyline".equals(polyLine.name)) {
                            this.currObject.polyline = polyLine.points;
                        } else if ("polygon".equals(polyLine.name)) {
                            this.currObject.polygon = polyLine.points;
                        }
                    }
                }

                private void putProperty(Property property) {
                    if ("tile".equals(property.parentType)) {
                        map.setTileProperty(this.currTile + this.currTileSet.firstgid, property.name, property.value);
                    } else if ("map".equals(property.parentType)) {
                        map.properties.put(property.name, property.value);
                    } else if ("layer".equals(property.parentType)) {
                        this.currLayer.properties.put(property.name, property.value);
                    } else if ("objectgroup".equals(property.parentType)) {
                        this.currObjectGroup.properties.put(property.name, property.value);
                    } else if ("object".equals(property.parentType)) {
                        this.currObject.properties.put(property.name, property.value);
                    }
                }

                private void fromCSV() {
                    StringTokenizer st = new StringTokenizer(this.dataString.trim(), ",");
                    for (int row2 = 0; row2 < this.currLayerHeight; row2++) {
                        for (int col2 = 0; col2 < this.currLayerWidth; col2++) {
                            this.currLayer.tiles[row2][col2] = (int) Long.parseLong(st.nextToken().trim());
                        }
                    }
                }

                private void arrangeData() {
                    int byteCounter = 0;
                    for (int row2 = 0; row2 < this.currLayerHeight; row2++) {
                        for (int col2 = 0; col2 < this.currLayerWidth; col2++) {
                            int byteCounter2 = byteCounter + 1;
                            int byteCounter3 = byteCounter2 + 1;
                            int byteCounter4 = byteCounter3 + 1;
                            byteCounter = byteCounter4 + 1;
                            this.currLayer.tiles[row2][col2] = TiledLoader.unsignedByteToInt(this.data[byteCounter]) | (TiledLoader.unsignedByteToInt(this.data[byteCounter2]) << 8) | (TiledLoader.unsignedByteToInt(this.data[byteCounter3]) << 16) | (TiledLoader.unsignedByteToInt(this.data[byteCounter4]) << 24);
                        }
                    }
                }

                private void unZlib() {
                    Inflater zlib = new Inflater();
                    byte[] readTemp = new byte[4];
                    zlib.setInput(this.data, 0, this.data.length);
                    for (int row2 = 0; row2 < this.currLayerHeight; row2++) {
                        int col2 = 0;
                        while (col2 < this.currLayerWidth) {
                            try {
                                zlib.inflate(readTemp, 0, 4);
                                this.currLayer.tiles[row2][col2] = TiledLoader.unsignedByteToInt(readTemp[0]) | (TiledLoader.unsignedByteToInt(readTemp[1]) << 8) | (TiledLoader.unsignedByteToInt(readTemp[2]) << 16) | (TiledLoader.unsignedByteToInt(readTemp[3]) << 24);
                                col2++;
                            } catch (DataFormatException e) {
                                throw new GdxRuntimeException("Error Reading TMX Layer Data.", e);
                            }
                        }
                    }
                }

                private void unGZip() {
                    try {
                        GZIPInputStream GZIS = new GZIPInputStream(new ByteArrayInputStream(this.data), this.data.length);
                        byte[] readTemp = new byte[4];
                        for (int row2 = 0; row2 < this.currLayerHeight; row2++) {
                            int col2 = 0;
                            while (col2 < this.currLayerWidth) {
                                try {
                                    GZIS.read(readTemp, 0, 4);
                                    this.currLayer.tiles[row2][col2] = TiledLoader.unsignedByteToInt(readTemp[0]) | (TiledLoader.unsignedByteToInt(readTemp[1]) << 8) | (TiledLoader.unsignedByteToInt(readTemp[2]) << 16) | (TiledLoader.unsignedByteToInt(readTemp[3]) << 24);
                                    col2++;
                                } catch (IOException e) {
                                    throw new GdxRuntimeException("Error Reading TMX Layer Data.", e);
                                }
                            }
                        }
                    } catch (IOException e2) {
                        throw new GdxRuntimeException("Error Reading TMX Layer Data - IOException: " + e2.getMessage());
                    }
                }
            };
            if (tmxFile != null) {
                xmlReader.parse(tmxFile);
            } else {
                xmlReader.parse(tmxData);
            }
            return map;
        } catch (IOException e) {
            throw new GdxRuntimeException("Error Parsing TMX file", e);
        }
    }

    static int unsignedByteToInt(byte b) {
        return b & 255;
    }
}
