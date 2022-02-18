package cn.infocore.dto;

import lombok.Data;

@Data
public class EcsDTO {
	
    Integer id;
    
    String ecsId;
    
    String userId;

    String dataArkId;

    Integer type;

    String name;

    String exceptions;

    String ak;

    String sk;

    String ossAk;

    String ossSk;

    String ossBucket;

}
