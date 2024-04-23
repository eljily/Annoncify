package com.sibrahim.annoncify.config;

import com.sibrahim.annoncify.entity.Region;
import com.sibrahim.annoncify.entity.SubRegion;
import com.sibrahim.annoncify.repository.RegionRepository;
import com.sibrahim.annoncify.repository.SubRegionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
//@Configuration
public class DataRegionInitializer {

    private final RegionRepository regionRepository;
    private final SubRegionRepository subRegionRepository;

    public DataRegionInitializer(RegionRepository regionRepository, SubRegionRepository subRegionRepository) {
        this.regionRepository = regionRepository;
        this.subRegionRepository = subRegionRepository;
    }

   // @Bean
    public CommandLineRunner initializeRegionsData() {
        return args -> {
            initializeRegionsAndSubRegions();
        };
    }

    private void initializeRegionsAndSubRegions() {
        // Define regions and their subregions
        List<String> regionNames = List.of("Nouakchott", "Dakhlet Nouadhibou", "Tagant","Adrar","Tiris Zemmour","Inchiri",
                "Hodh El Gharbi","Gorgol","Assaba","Brakna","Hodh Ech Chargui","Trarza","Guidimaka");
        List<List<String>> subRegionNames = List.of(
                List.of("Arafat", "Dar Naim", "El Mina","Ksar","Riyadh","Sebkha","Tevragh Zeïna","Teyareth","Toujounine"),
                List.of("Nouadhibou", "Boulenouar","Inal","Tmeimichatt","Nouamghar"),
                List.of("Moudjeria", "Tichitt", "Tidjikdja"),
                List.of("Aoujeft", "Atar", "Chinguetti","Ouadane"),
                List.of("Bir Moghreïn", "F'Derick","Zouerate"),
                List.of("Akjoujt"),
                List.of("Aïoun El Atrouss", "Kobenni", "Tamchekett","Tintane"),
                List.of("Kaédi", "Lexeiba","M'Bout","Maghama","Monguel"),
                List.of("Barkewol", "Boumdeid", "Guerou","Kankossa","Kiffa"),
                List.of("Aleg", "Bababé", "Boghé","M'Bagne","Magta-Lahjar"),
                List.of("Amourj", "Bassikounou","Djiguenni","Néma","Oualata","Timbedra"),
                List.of("Boutilimit", "Keur Macène", "Mederdra","Ouad Naga","R'Kiz","Rosso"),
                List.of("Ghabou", "Ould Yengé", "Sélibabi","Wompou")
        );

        // Initialize regions and subregions
        for (int i = 0; i < regionNames.size(); i++) {
            String regionName = regionNames.get(i);
            List<String> regionSubRegions = subRegionNames.get(i);

            // Initialize region if it doesn't exist
            Region region = regionRepository.findByName(regionName).orElseGet(() -> {
                Region newRegion = Region.builder().name(regionName).build();
                return regionRepository.save(newRegion);
            });

            // Initialize subregions for the current region
            for (String subRegionName : regionSubRegions) {
                initializeSubRegion(subRegionName, region);
            }
        }
    }

    private void initializeSubRegion(String subRegionName, Region region) {
        Optional<SubRegion> existingSubRegion = subRegionRepository.findByRegionAndName(region, subRegionName);
        if (existingSubRegion.isEmpty()) {
            SubRegion newSubRegion = SubRegion.builder().name(subRegionName).region(region).build();
            subRegionRepository.save(newSubRegion);
        }
    }
}
