public class Camera {
    Vector3 position;
    Vector3 lookAt;
    double fieldOfView;
    Camera(Vector3 position, Vector3 lookAt, double fieldOfView) {
        this.position = position;
        this.lookAt = lookAt;
        this.fieldOfView = fieldOfView;
    }
}
