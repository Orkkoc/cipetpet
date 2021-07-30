package aurelienribon.bodyeditor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.OrderedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BodyEditorLoader {
    private final CircleShape circleShape = new CircleShape();
    private final Model model;
    private final PolygonShape polygonShape = new PolygonShape();
    private final Vector2 vec = new Vector2();
    private final List<Vector2> vectorPool = new ArrayList();

    public static class CircleModel {
        public final Vector2 center = new Vector2();
        public float radius;
    }

    public static class Model {
        public final Map<String, RigidBodyModel> rigidBodies = new HashMap();
    }

    public static class PolygonModel {
        /* access modifiers changed from: private */
        public Vector2[] buffer;
        public final List<Vector2> vertices = new ArrayList();
    }

    public static class RigidBodyModel {
        public final List<CircleModel> circles = new ArrayList();
        public String imagePath;
        public String name;
        public final Vector2 origin = new Vector2();
        public final List<PolygonModel> polygons = new ArrayList();
    }

    public BodyEditorLoader(FileHandle file) {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        this.model = readJson(file.readString());
    }

    public BodyEditorLoader(String str) {
        if (str == null) {
            throw new NullPointerException("str is null");
        }
        this.model = readJson(str);
    }

    public void attachFixture(Body body, String name, FixtureDef fd, float scale) {
        RigidBodyModel rbModel = this.model.rigidBodies.get(name);
        if (rbModel == null) {
            throw new RuntimeException("Name '" + name + "' was not found.");
        }
        Vector2 origin = this.vec.set(rbModel.origin).mul(scale);
        int n = rbModel.polygons.size();
        for (int i = 0; i < n; i++) {
            PolygonModel polygon = rbModel.polygons.get(i);
            Vector2[] vertices = polygon.buffer;
            int nn = vertices.length;
            for (int ii = 0; ii < nn; ii++) {
                vertices[ii] = newVec().set(polygon.vertices.get(ii)).mul(scale);
                vertices[ii].sub(origin);
            }
            this.polygonShape.set(vertices);
            fd.shape = this.polygonShape;
            body.createFixture(fd);
            for (Vector2 free : vertices) {
                free(free);
            }
        }
        int n2 = rbModel.circles.size();
        for (int i2 = 0; i2 < n2; i2++) {
            CircleModel circle = rbModel.circles.get(i2);
            Vector2 center = newVec().set(circle.center).mul(scale);
            float radius = circle.radius * scale;
            this.circleShape.setPosition(center);
            this.circleShape.setRadius(radius);
            fd.shape = this.circleShape;
            body.createFixture(fd);
            free(center);
        }
    }

    public String getImagePath(String name) {
        RigidBodyModel rbModel = this.model.rigidBodies.get(name);
        if (rbModel != null) {
            return rbModel.imagePath;
        }
        throw new RuntimeException("Name '" + name + "' was not found.");
    }

    public Vector2 getOrigin(String name, float scale) {
        RigidBodyModel rbModel = this.model.rigidBodies.get(name);
        if (rbModel != null) {
            return this.vec.set(rbModel.origin).mul(scale);
        }
        throw new RuntimeException("Name '" + name + "' was not found.");
    }

    public Model getInternalModel() {
        return this.model;
    }

    private Model readJson(String str) {
        Model m = new Model();
        Array<?> bodiesElems = (Array) ((OrderedMap) new JsonReader().parse(str)).get("rigidBodies");
        for (int i = 0; i < bodiesElems.size; i++) {
            RigidBodyModel rbModel = readRigidBody((OrderedMap) bodiesElems.get(i));
            m.rigidBodies.put(rbModel.name, rbModel);
        }
        return m;
    }

    private RigidBodyModel readRigidBody(OrderedMap<String, ?> bodyElem) {
        RigidBodyModel rbModel = new RigidBodyModel();
        rbModel.name = (String) bodyElem.get("name");
        rbModel.imagePath = (String) bodyElem.get("imagePath");
        OrderedMap<String, ?> originElem = (OrderedMap) bodyElem.get("origin");
        rbModel.origin.f165x = ((Float) originElem.get("x")).floatValue();
        rbModel.origin.f166y = ((Float) originElem.get("y")).floatValue();
        Array<?> polygonsElem = (Array) bodyElem.get("polygons");
        for (int i = 0; i < polygonsElem.size; i++) {
            PolygonModel polygon = new PolygonModel();
            rbModel.polygons.add(polygon);
            Array<?> verticesElem = (Array) polygonsElem.get(i);
            for (int ii = 0; ii < verticesElem.size; ii++) {
                OrderedMap<String, ?> vertexElem = (OrderedMap) verticesElem.get(ii);
                polygon.vertices.add(new Vector2(((Float) vertexElem.get("x")).floatValue(), ((Float) vertexElem.get("y")).floatValue()));
            }
            Vector2[] unused = polygon.buffer = new Vector2[polygon.vertices.size()];
        }
        Array<?> circlesElem = (Array) bodyElem.get("circles");
        for (int i2 = 0; i2 < circlesElem.size; i2++) {
            CircleModel circle = new CircleModel();
            rbModel.circles.add(circle);
            OrderedMap<String, ?> circleElem = (OrderedMap) circlesElem.get(i2);
            circle.center.f165x = ((Float) circleElem.get("cx")).floatValue();
            circle.center.f166y = ((Float) circleElem.get("cy")).floatValue();
            circle.radius = ((Float) circleElem.get("r")).floatValue();
        }
        return rbModel;
    }

    private Vector2 newVec() {
        return this.vectorPool.isEmpty() ? new Vector2() : this.vectorPool.remove(0);
    }

    private void free(Vector2 v) {
        this.vectorPool.add(v);
    }
}
