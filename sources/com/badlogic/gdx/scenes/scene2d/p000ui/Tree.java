package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Tree */
public class Tree extends WidgetGroup {
    private ClickListener clickListener;
    private Node foundNode;
    float iconSpacing;
    float indentSpacing;
    private float leftColumnWidth;
    boolean multiSelect;
    Node overNode;
    float padding;
    private float prefHeight;
    private float prefWidth;
    final Array<Node> rootNodes;
    final Array<Node> selectedNodes;
    private boolean sizeInvalid;
    TreeStyle style;
    float ySpacing;

    public Tree(Skin skin) {
        this((TreeStyle) skin.get(TreeStyle.class));
    }

    public Tree(Skin skin, String styleName) {
        this((TreeStyle) skin.get(styleName, TreeStyle.class));
    }

    public Tree(TreeStyle style2) {
        this.rootNodes = new Array<>();
        this.selectedNodes = new Array<>();
        this.ySpacing = 4.0f;
        this.iconSpacing = 2.0f;
        this.padding = 0.0f;
        this.sizeInvalid = true;
        this.multiSelect = true;
        setStyle(style2);
        initialize();
    }

    private void initialize() {
        C01101 r0 = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                boolean z = true;
                Node node = Tree.this.getNodeAt(y);
                if (node == null || node != Tree.this.getNodeAt(getTouchDownY())) {
                    return;
                }
                if (!Tree.this.multiSelect || Tree.this.selectedNodes.size <= 0 || (!Gdx.input.isKeyPressed(59) && !Gdx.input.isKeyPressed(60))) {
                    if (!Tree.this.multiSelect || (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT))) {
                        if (node.children.size > 0) {
                            float rowX = node.actor.getX();
                            if (node.icon != null) {
                                rowX -= Tree.this.iconSpacing + node.icon.getMinWidth();
                            }
                            if (x < rowX) {
                                if (node.expanded) {
                                    z = false;
                                }
                                node.setExpanded(z);
                                return;
                            }
                        }
                        if (node.isSelectable()) {
                            Tree.this.selectedNodes.clear();
                        } else {
                            return;
                        }
                    } else if (!node.isSelectable()) {
                        return;
                    }
                    if (!Tree.this.selectedNodes.removeValue(node, true)) {
                        Tree.this.selectedNodes.add(node);
                    }
                    ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
                    Tree.this.fire(changeEvent);
                    Pools.free(changeEvent);
                    return;
                }
                float low = Tree.this.selectedNodes.first().actor.getY();
                float high = node.actor.getY();
                if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                    Tree.this.selectedNodes.clear();
                }
                if (low > high) {
                    Tree.this.selectNodes(Tree.this.rootNodes, high, low);
                } else {
                    Tree.this.selectNodes(Tree.this.rootNodes, low, high);
                }
                ChangeListener.ChangeEvent changeEvent2 = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
                Tree.this.fire(changeEvent2);
                Pools.free(changeEvent2);
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                Tree.this.setOverNode(Tree.this.getNodeAt(y));
                return false;
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (toActor == null || !toActor.isDescendantOf(Tree.this)) {
                    Tree.this.setOverNode((Node) null);
                }
            }
        };
        this.clickListener = r0;
        addListener(r0);
    }

    public void setStyle(TreeStyle style2) {
        this.style = style2;
        this.indentSpacing = Math.max(style2.plus.getMinWidth(), style2.minus.getMinWidth()) + this.iconSpacing;
    }

    public void add(Node node) {
        insert(this.rootNodes.size, node);
    }

    public void insert(int index, Node node) {
        remove(node);
        node.parent = null;
        this.rootNodes.insert(index, node);
        node.addToTree(this);
        invalidateHierarchy();
    }

    public void remove(Node node) {
        if (node.parent != null) {
            node.parent.remove(node);
            return;
        }
        this.rootNodes.removeValue(node, true);
        node.removeFromTree(this);
        invalidateHierarchy();
    }

    public void clear() {
        super.clear();
        this.rootNodes.clear();
        this.selectedNodes.clear();
        setOverNode((Node) null);
    }

    public Array<Node> getNodes() {
        return this.rootNodes;
    }

    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }

    private void computeSize() {
        this.sizeInvalid = false;
        this.prefWidth = this.style.plus.getMinWidth();
        this.prefWidth = Math.max(this.prefWidth, this.style.minus.getMinWidth());
        this.prefHeight = getHeight();
        this.leftColumnWidth = 0.0f;
        computeSize(this.rootNodes, this.indentSpacing);
        this.leftColumnWidth += this.iconSpacing + this.padding;
        this.prefWidth += this.leftColumnWidth + this.padding;
        this.prefHeight = getHeight() - this.prefHeight;
    }

    private void computeSize(Array<Node> nodes, float indent) {
        float rowWidth;
        float ySpacing2 = this.ySpacing;
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            float rowWidth2 = indent + this.iconSpacing;
            Actor actor = node.actor;
            if (actor instanceof Layout) {
                Layout layout = (Layout) actor;
                rowWidth = rowWidth2 + layout.getPrefWidth();
                node.height = layout.getPrefHeight();
                layout.pack();
            } else {
                rowWidth = rowWidth2 + actor.getWidth();
                node.height = actor.getHeight();
            }
            if (node.icon != null) {
                rowWidth += (this.iconSpacing * 2.0f) + node.icon.getMinWidth();
                node.height = Math.max(node.height, node.icon.getMinHeight());
            }
            this.prefWidth = Math.max(this.prefWidth, rowWidth);
            this.prefHeight -= node.height + ySpacing2;
            if (node.expanded) {
                computeSize(node.children, this.indentSpacing + indent);
            }
        }
    }

    public void layout() {
        if (this.sizeInvalid) {
            computeSize();
        }
        layout(this.rootNodes, this.leftColumnWidth + this.indentSpacing + this.iconSpacing, getHeight() - (this.ySpacing / 2.0f));
    }

    private float layout(Array<Node> nodes, float indent, float y) {
        float ySpacing2 = this.ySpacing;
        Drawable drawable = this.style.plus;
        Drawable drawable2 = this.style.minus;
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            Actor actor = node.actor;
            float x = indent;
            if (node.icon != null) {
                x += node.icon.getMinWidth();
            }
            float y2 = y - node.height;
            node.actor.setPosition(x, y2);
            y = y2 - ySpacing2;
            if (node.expanded) {
                y = layout(node.children, this.indentSpacing + indent, y);
            }
        }
        return y;
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        Color color = getColor();
        if (this.style.background != null) {
            batch.setColor(color.f70r, color.f69g, color.f68b, color.f67a * parentAlpha);
            this.style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
            batch.setColor(Color.WHITE);
        }
        draw(batch, this.rootNodes, this.leftColumnWidth);
        super.draw(batch, parentAlpha);
    }

    private void draw(SpriteBatch batch, Array<Node> nodes, float indent) {
        Drawable expandIcon;
        Drawable plus = this.style.plus;
        Drawable minus = this.style.minus;
        float x = getX();
        float y = getY();
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            Actor actor = node.actor;
            if (this.selectedNodes.contains(node, true) && this.style.selection != null) {
                this.style.selection.draw(batch, x, (actor.getY() + y) - (this.ySpacing / 2.0f), getWidth(), this.ySpacing + node.height);
            } else if (node == this.overNode && this.style.over != null) {
                this.style.over.draw(batch, x, (actor.getY() + y) - (this.ySpacing / 2.0f), getWidth(), this.ySpacing + node.height);
            }
            if (node.icon != null) {
                float iconY = actor.getY() + ((float) Math.round((node.height - node.icon.getMinHeight()) / 2.0f));
                batch.setColor(actor.getColor());
                node.icon.draw(batch, ((node.actor.getX() + x) - this.iconSpacing) - node.icon.getMinWidth(), y + iconY, node.icon.getMinWidth(), node.icon.getMinHeight());
                batch.setColor(Color.WHITE);
            }
            if (node.children.size != 0) {
                if (node.expanded) {
                    expandIcon = minus;
                } else {
                    expandIcon = plus;
                }
                SpriteBatch spriteBatch = batch;
                expandIcon.draw(spriteBatch, (x + indent) - this.iconSpacing, y + actor.getY() + ((float) Math.round((node.height - expandIcon.getMinHeight()) / 2.0f)), expandIcon.getMinWidth(), expandIcon.getMinHeight());
                if (node.expanded) {
                    draw(batch, node.children, this.indentSpacing + indent);
                }
            }
        }
    }

    public Node getNodeAt(float y) {
        this.foundNode = null;
        getNodeAt(this.rootNodes, y, getHeight());
        return this.foundNode;
    }

    private float getNodeAt(Array<Node> nodes, float y, float rowY) {
        int i = 0;
        int n = nodes.size;
        while (i < n) {
            Node node = nodes.get(i);
            if (y < (rowY - node.height) - this.ySpacing || y >= rowY) {
                rowY -= node.height + this.ySpacing;
                if (node.expanded) {
                    rowY = getNodeAt(node.children, y, rowY);
                    if (rowY == -1.0f) {
                        return -1.0f;
                    }
                }
                i++;
            } else {
                this.foundNode = node;
                return -1.0f;
            }
        }
        return rowY;
    }

    /* access modifiers changed from: package-private */
    public void selectNodes(Array<Node> nodes, float low, float high) {
        float f = this.ySpacing;
        int i = 0;
        int n = nodes.size;
        while (i < n) {
            Node node = nodes.get(i);
            if (node.actor.getY() >= low) {
                if (node.isSelectable()) {
                    if (node.actor.getY() <= high) {
                        this.selectedNodes.add(node);
                    }
                    if (node.expanded) {
                        selectNodes(node.children, low, high);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public Array<Node> getSelection() {
        return this.selectedNodes;
    }

    public void setSelection(Node node) {
        this.selectedNodes.clear();
        this.selectedNodes.add(node);
    }

    public void setSelection(Array<Node> nodes) {
        this.selectedNodes.clear();
        this.selectedNodes.addAll((Array) nodes);
    }

    public void addSelection(Node node) {
        this.selectedNodes.add(node);
    }

    public void clearSelection() {
        this.selectedNodes.clear();
    }

    public TreeStyle getStyle() {
        return this.style;
    }

    public Array<Node> getRootNodes() {
        return this.rootNodes;
    }

    public Node getOverNode() {
        return this.overNode;
    }

    public void setOverNode(Node overNode2) {
        this.overNode = overNode2;
    }

    public void setPadding(float padding2) {
        this.padding = padding2;
    }

    public void setYSpacing(float ySpacing2) {
        this.ySpacing = ySpacing2;
    }

    public void setIconSpacing(float iconSpacing2) {
        this.iconSpacing = iconSpacing2;
    }

    public float getPrefWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefWidth;
    }

    public float getPrefHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefHeight;
    }

    public Node findNode(Object object) {
        if (object != null) {
            return findNode(this.rootNodes, object);
        }
        throw new IllegalArgumentException("object cannot be null.");
    }

    static Node findNode(Array<Node> nodes, Object object) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            if (object.equals(node.object)) {
                return node;
            }
        }
        int n2 = nodes.size;
        for (int i2 = 0; i2 < n2; i2++) {
            Node found = findNode(nodes.get(i2).children, object);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void collapseAll() {
        collapseAll(this.rootNodes);
    }

    static void collapseAll(Array<Node> nodes) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            node.setExpanded(false);
            collapseAll(node.children);
        }
    }

    public void expandAll() {
        expandAll(this.rootNodes);
    }

    static void expandAll(Array<Node> nodes) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            nodes.get(i).expandAll();
        }
    }

    public ClickListener getClickListener() {
        return this.clickListener;
    }

    public void setMultiSelect(boolean multiSelect2) {
        this.multiSelect = multiSelect2;
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Tree$Node */
    public static class Node {
        Actor actor;
        final Array<Node> children = new Array<>(0);
        boolean expanded;
        float height;
        Drawable icon;
        Object object;
        Node parent;
        boolean selectable = true;

        public Node(Actor actor2) {
            if (actor2 == null) {
                throw new IllegalArgumentException("actor cannot be null.");
            }
            this.actor = actor2;
        }

        public void setExpanded(boolean expanded2) {
            Tree tree;
            if (expanded2 != this.expanded) {
                this.expanded = expanded2;
                if (this.children.size != 0 && (tree = getTree()) != null) {
                    if (expanded2) {
                        int n = this.children.size;
                        for (int i = 0; i < n; i++) {
                            this.children.get(i).addToTree(tree);
                        }
                    } else {
                        int n2 = this.children.size;
                        for (int i2 = 0; i2 < n2; i2++) {
                            this.children.get(i2).removeFromTree(tree);
                        }
                    }
                    tree.invalidateHierarchy();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void addToTree(Tree tree) {
            tree.addActor(this.actor);
            if (this.expanded) {
                int n = this.children.size;
                for (int i = 0; i < n; i++) {
                    this.children.get(i).addToTree(tree);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void removeFromTree(Tree tree) {
            tree.removeActor(this.actor);
            if (this.expanded) {
                int n = this.children.size;
                for (int i = 0; i < n; i++) {
                    this.children.get(i).removeFromTree(tree);
                }
            }
        }

        public void add(Node node) {
            insert(this.children.size, node);
        }

        public void addAll(Array<Node> nodes) {
            int n = nodes.size;
            for (int i = 0; i < n; i++) {
                insert(this.children.size, nodes.get(i));
            }
        }

        public void insert(int index, Node node) {
            Tree tree;
            node.parent = this;
            this.children.insert(index, node);
            if (this.expanded && (tree = getTree()) != null) {
                int n = this.children.size;
                for (int i = 0; i < n; i++) {
                    this.children.get(i).addToTree(tree);
                }
            }
        }

        public void remove() {
            Tree tree = getTree();
            if (tree != null) {
                tree.remove(this);
            }
        }

        public void remove(Node node) {
            Tree tree;
            this.children.removeValue(node, true);
            if (this.expanded && (tree = getTree()) != null) {
                node.removeFromTree(tree);
                if (this.children.size == 0) {
                    this.expanded = false;
                }
            }
        }

        public void removeAll() {
            Tree tree = getTree();
            if (tree != null) {
                int n = this.children.size;
                for (int i = 0; i < n; i++) {
                    this.children.get(i).removeFromTree(tree);
                }
            }
            this.children.clear();
        }

        public Tree getTree() {
            Group parent2 = this.actor.getParent();
            if (!(parent2 instanceof Tree)) {
                return null;
            }
            return (Tree) parent2;
        }

        public Actor getActor() {
            return this.actor;
        }

        public boolean isExpanded() {
            return this.expanded;
        }

        public Array<Node> getChildren() {
            return this.children;
        }

        public Node getParent() {
            return this.parent;
        }

        public void setIcon(Drawable icon2) {
            this.icon = icon2;
        }

        public Object getObject() {
            return this.object;
        }

        public void setObject(Object object2) {
            this.object = object2;
        }

        public Drawable getIcon() {
            return this.icon;
        }

        /* Debug info: failed to restart local var, previous not found, register: 2 */
        public Node findNode(Object object2) {
            if (object2 != null) {
                return object2.equals(this.object) ? this : Tree.findNode(this.children, object2);
            }
            throw new IllegalArgumentException("object cannot be null.");
        }

        public void collapseAll() {
            setExpanded(false);
            Tree.collapseAll(this.children);
        }

        public void expandAll() {
            setExpanded(true);
            if (this.children.size > 0) {
                Tree.expandAll(this.children);
            }
        }

        public void expandTo() {
            for (Node node = this.parent; node != null; node = node.parent) {
                node.setExpanded(true);
            }
        }

        public boolean isSelectable() {
            return this.selectable;
        }

        public void setSelectable(boolean selectable2) {
            this.selectable = selectable2;
        }
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Tree$TreeStyle */
    public static class TreeStyle {
        public Drawable background;
        public Drawable minus;
        public Drawable over;
        public Drawable plus;
        public Drawable selection;

        public TreeStyle() {
        }

        public TreeStyle(Drawable plus2, Drawable minus2, Drawable selection2) {
            this.plus = plus2;
            this.minus = minus2;
            this.selection = selection2;
        }

        public TreeStyle(TreeStyle style) {
            this.plus = style.plus;
            this.minus = style.minus;
            this.selection = style.selection;
        }
    }
}
