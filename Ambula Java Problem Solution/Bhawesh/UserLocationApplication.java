package Bhawesh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.Generated;
import javax.persistence.*;
import javax.smartcardio.ResponseAPDU;

import java.net.ResponseCache;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
class UserLocation {
    @Id
    @Generated(date = Generated.IDENTITY, value = { "" })
    private Long id;
    private String name;
    private double latitude;
    private double longitude;
    private boolean excluded;


    public double calculateDistanceFromOrigin() {
        return Math.sqrt(latitude * latitude + longitude * longitude);
    }
}

@Repository
interface UserLocationRepository extends JpaRepository<UserLocation, Long> {

    UserLocation save(UserLocation userLocation);

    List<UserLocation> findAll();
}

@Service
class UserLocationService {
    @Autowired
    private UserLocationRepository userLocationRepository;

    public void createTable() {
       
    }

    public UserLocation updateUserLocation(UserLocation userLocation) {
        return userLocationRepository.save(userLocation);
    }

    public List<UserLocation> getNearestUsers(int count) {
        List<UserLocation> allUsers = userLocationRepository.findAll();

        List<UserLocation> nearestUsers = new ArrayList<>(allUsers);

        nearestUsers.sort(Comparator.comparingDouble(UserLocation::calculateDistanceFromOrigin));

        if (nearestUsers.size() > count) {
            nearestUsers = nearestUsers.subList(0, count);
        }

        return nearestUsers;
    }
}

@RestController
@RequestMapping("/api")
class UserController {
    @Autowired
    private UserLocationService userLocationService;

    @PostMapping("create_data")
    public ResponseCache<String> createData() {
        userLocationService.createTable();
        return ResponseEntity.ok("Table created successfully");
    }

    @PostMapping("/update_data")
    public ResponseEntity<List<UserLocation>> updateData(@RequestMapping(value = "") UserLocation userLocation) {
        UserLocation updatedLocation = userLocationService.updateUserLocation(userLocation);
        return (ResponseEntity<List<UserLocation>>) ResponseEntity.ok(updatedLocation);
    }

    @GetMapping("/get_users/{count}")
    public ResponseEntity<List<UserLocation>> getUsers(@PathVariable int count) {
        List<UserLocation> nearestUsers = userLocationService.getNearestUsers(count);
        return ResponseEntity.ok(nearestUsers);
    }
}

@SpringBootApplication
public class UserLocationApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserLocationApplication.class, args);
    }
}
