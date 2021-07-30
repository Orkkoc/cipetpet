package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.OrderedMap;
import java.util.Iterator;

public class PixmapPacker implements Disposable {
    Page currPage;
    boolean disposed;
    final boolean duplicateBorder;
    final int padding;
    final Pixmap.Format pageFormat;
    final int pageHeight;
    final int pageWidth;
    final Array<Page> pages = new Array<>();

    static final class Node {
        public String leaveName;
        public Node leftChild;
        public Rectangle rect;
        public Node rightChild;

        public Node(int x, int y, int width, int height, Node leftChild2, Node rightChild2, String leaveName2) {
            this.rect = new Rectangle((float) x, (float) y, (float) width, (float) height);
            this.leftChild = leftChild2;
            this.rightChild = rightChild2;
            this.leaveName = leaveName2;
        }

        public Node() {
            this.rect = new Rectangle();
        }
    }

    public class Page {
        Array<String> addedRects = new Array<>();
        Pixmap image;
        OrderedMap<String, Rectangle> rects;
        Node root;
        Texture texture;

        public Page() {
        }

        public Pixmap getPixmap() {
            return this.image;
        }
    }

    public PixmapPacker(int width, int height, Pixmap.Format format, int padding2, boolean duplicateBorder2) {
        this.pageWidth = width;
        this.pageHeight = height;
        this.pageFormat = format;
        this.padding = padding2;
        this.duplicateBorder = duplicateBorder2;
        newPage();
    }

    public synchronized Rectangle pack(String name, Pixmap image) {
        Rectangle rect;
        if (this.disposed) {
            rect = null;
        } else if (getRect(name) != null) {
            throw new RuntimeException("Key with name '" + name + "' is already in map");
        } else {
            int borderPixels = (this.padding + (this.duplicateBorder ? 1 : 0)) << 1;
            Rectangle rectangle = new Rectangle(0.0f, 0.0f, (float) (image.getWidth() + borderPixels), (float) (image.getHeight() + borderPixels));
            if (rectangle.getWidth() > ((float) this.pageWidth) || rectangle.getHeight() > ((float) this.pageHeight)) {
                throw new GdxRuntimeException("page size for '" + name + "' to small");
            }
            Node node = insert(this.currPage.root, rectangle);
            if (node == null) {
                newPage();
                rect = pack(name, image);
            } else {
                node.leaveName = name;
                rect = new Rectangle(node.rect);
                rect.width -= (float) borderPixels;
                rect.height -= (float) borderPixels;
                int borderPixels2 = borderPixels >> 1;
                rect.f161x += (float) borderPixels2;
                rect.f162y += (float) borderPixels2;
                this.currPage.rects.put(name, rect);
                Pixmap.Blending blending = Pixmap.getBlending();
                Pixmap.setBlending(Pixmap.Blending.None);
                this.currPage.image.drawPixmap(image, (int) rect.f161x, (int) rect.f162y);
                if (this.duplicateBorder) {
                    int imageWidth = image.getWidth();
                    int imageHeight = image.getHeight();
                    this.currPage.image.drawPixmap(image, 0, 0, 1, 1, ((int) rect.f161x) - 1, ((int) rect.f162y) - 1, 1, 1);
                    this.currPage.image.drawPixmap(image, imageWidth - 1, 0, 1, 1, ((int) rect.width) + ((int) rect.f161x), ((int) rect.f162y) - 1, 1, 1);
                    this.currPage.image.drawPixmap(image, 0, imageHeight - 1, 1, 1, ((int) rect.f161x) - 1, ((int) rect.height) + ((int) rect.f162y), 1, 1);
                    this.currPage.image.drawPixmap(image, imageWidth - 1, imageHeight - 1, 1, 1, ((int) rect.width) + ((int) rect.f161x), ((int) rect.height) + ((int) rect.f162y), 1, 1);
                    this.currPage.image.drawPixmap(image, 0, 0, imageWidth, 1, (int) rect.f161x, ((int) rect.f162y) - 1, (int) rect.width, 1);
                    this.currPage.image.drawPixmap(image, 0, imageHeight - 1, imageWidth, 1, (int) rect.f161x, ((int) rect.f162y) + ((int) rect.height), (int) rect.width, 1);
                    this.currPage.image.drawPixmap(image, 0, 0, 1, imageHeight, ((int) rect.f161x) - 1, (int) rect.f162y, 1, (int) rect.height);
                    this.currPage.image.drawPixmap(image, imageWidth - 1, 0, 1, imageHeight, ((int) rect.f161x) + ((int) rect.width), (int) rect.f162y, 1, (int) rect.height);
                }
                Pixmap.setBlending(blending);
                this.currPage.addedRects.add(name);
            }
        }
        return rect;
    }

    private void newPage() {
        Page page = new Page();
        page.image = new Pixmap(this.pageWidth, this.pageHeight, this.pageFormat);
        page.root = new Node(0, 0, this.pageWidth, this.pageHeight, (Node) null, (Node) null, (String) null);
        page.rects = new OrderedMap<>();
        this.pages.add(page);
        this.currPage = page;
    }

    private Node insert(Node node, Rectangle rect) {
        if (node.leaveName == null && node.leftChild != null && node.rightChild != null) {
            Node newNode = insert(node.leftChild, rect);
            return newNode == null ? insert(node.rightChild, rect) : newNode;
        } else if (node.leaveName != null) {
            return null;
        } else {
            if (node.rect.width == rect.width && node.rect.height == rect.height) {
                return node;
            }
            if (node.rect.width < rect.width || node.rect.height < rect.height) {
                return null;
            }
            node.leftChild = new Node();
            node.rightChild = new Node();
            if (((int) node.rect.width) - ((int) rect.width) > ((int) node.rect.height) - ((int) rect.height)) {
                node.leftChild.rect.f161x = node.rect.f161x;
                node.leftChild.rect.f162y = node.rect.f162y;
                node.leftChild.rect.width = rect.width;
                node.leftChild.rect.height = node.rect.height;
                node.rightChild.rect.f161x = node.rect.f161x + rect.width;
                node.rightChild.rect.f162y = node.rect.f162y;
                node.rightChild.rect.width = node.rect.width - rect.width;
                node.rightChild.rect.height = node.rect.height;
            } else {
                node.leftChild.rect.f161x = node.rect.f161x;
                node.leftChild.rect.f162y = node.rect.f162y;
                node.leftChild.rect.width = node.rect.width;
                node.leftChild.rect.height = rect.height;
                node.rightChild.rect.f161x = node.rect.f161x;
                node.rightChild.rect.f162y = node.rect.f162y + rect.height;
                node.rightChild.rect.width = node.rect.width;
                node.rightChild.rect.height = node.rect.height - rect.height;
            }
            return insert(node.leftChild, rect);
        }
    }

    public Array<Page> getPages() {
        return this.pages;
    }

    public synchronized Rectangle getRect(String name) {
        Rectangle rect;
        Iterator i$ = this.pages.iterator();
        while (true) {
            if (!i$.hasNext()) {
                rect = null;
                break;
            }
            rect = i$.next().rects.get(name);
            if (rect != null) {
                break;
            }
        }
        return rect;
    }

    public synchronized Page getPage(String name) {
        Page page;
        Iterator i$ = this.pages.iterator();
        while (true) {
            if (!i$.hasNext()) {
                page = null;
                break;
            }
            page = i$.next();
            if (page.rects.get(name) != null) {
                break;
            }
        }
        return page;
    }

    public synchronized void dispose() {
        Iterator i$ = this.pages.iterator();
        while (i$.hasNext()) {
            i$.next().image.dispose();
        }
        this.disposed = true;
    }

    public synchronized TextureAtlas generateTextureAtlas(Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
        TextureAtlas atlas;
        atlas = new TextureAtlas();
        Iterator<Page> it = this.pages.iterator();
        while (it.hasNext()) {
            Page page = it.next();
            if (page.rects.size != 0) {
                Texture texture = new Texture(new ManagedPixmapTextureData(page.image, page.image.getFormat(), useMipMaps)) {
                    public void dispose() {
                        super.dispose();
                        getTextureData().consumePixmap().dispose();
                    }
                };
                texture.setFilter(minFilter, magFilter);
                Iterator i$ = page.rects.keys().iterator();
                while (i$.hasNext()) {
                    String name = i$.next();
                    Rectangle rect = page.rects.get(name);
                    atlas.addRegion(name, new TextureRegion(texture, (int) rect.f161x, (int) rect.f162y, (int) rect.width, (int) rect.height));
                }
            }
        }
        return atlas;
    }

    public synchronized void updateTextureAtlas(TextureAtlas atlas, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
        Iterator<Page> it = this.pages.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Page page = it.next();
            if (page.texture == null) {
                if (page.rects.size != 0 && page.addedRects.size > 0) {
                    page.texture = new Texture(new ManagedPixmapTextureData(page.image, page.image.getFormat(), useMipMaps)) {
                        public void dispose() {
                            super.dispose();
                            getTextureData().consumePixmap().dispose();
                        }
                    };
                    page.texture.setFilter(minFilter, magFilter);
                    Iterator i$ = page.addedRects.iterator();
                    while (i$.hasNext()) {
                        String name = i$.next();
                        Rectangle rect = page.rects.get(name);
                        atlas.addRegion(name, new TextureRegion(page.texture, (int) rect.f161x, (int) rect.f162y, (int) rect.width, (int) rect.height));
                    }
                    page.addedRects.clear();
                }
            } else if (page.addedRects.size > 0) {
                page.texture.load(page.texture.getTextureData());
                Iterator i$2 = page.addedRects.iterator();
                while (i$2.hasNext()) {
                    String name2 = i$2.next();
                    Rectangle rect2 = page.rects.get(name2);
                    atlas.addRegion(name2, new TextureRegion(page.texture, (int) rect2.f161x, (int) rect2.f162y, (int) rect2.width, (int) rect2.height));
                }
                page.addedRects.clear();
            }
        }
    }

    public int getPageWidth() {
        return this.pageWidth;
    }

    public int getPageHeight() {
        return this.pageHeight;
    }

    public int getPadding() {
        return this.padding;
    }

    public boolean duplicateBoarder() {
        return this.duplicateBorder;
    }

    public class ManagedPixmapTextureData extends PixmapTextureData {
        public ManagedPixmapTextureData(Pixmap pixmap, Pixmap.Format format, boolean useMipMaps) {
            super(pixmap, format, useMipMaps, false);
        }

        public boolean isManaged() {
            return true;
        }
    }
}
