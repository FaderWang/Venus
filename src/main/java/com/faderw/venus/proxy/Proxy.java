package com.faderw.venus.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FaderW
 * 2018/12/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proxy {

    private String host;
    private Integer port;
    private String username;
    private String password;
}
