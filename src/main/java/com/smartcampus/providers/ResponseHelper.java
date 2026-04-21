/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.providers;

/**
 *
 * @author mario
 */

import java.util.HashMap;
import java.util.Map;
public class ResponseHelper {
    public static Map<String, String> format(int status, String error) {
        Map<String, String> map = new HashMap<>();
        map.put("status", String.valueOf(status));
        map.put("error", error);
        return map;
    }
}