package pekko.product.api.v1.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class GetProductResponse extends BaseResponse {
    private String id;
    private String name;
    private Boolean active;
    private Boolean deleted;
    public GetProductResponse(Integer statusCode, String statusMessage){
        super(statusCode, statusMessage);
    }
    public GetProductResponse(String id, String name, Boolean active, Boolean deleted){
       super(null, null);
       this.id = id;
       this.name = name;
       this.active = active;
       this.deleted = deleted;
    }
}