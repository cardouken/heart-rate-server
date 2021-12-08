package ee.uustal.heartrateserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event implements Serializable {

    private String type;
    private Map<String, Object> body;

}