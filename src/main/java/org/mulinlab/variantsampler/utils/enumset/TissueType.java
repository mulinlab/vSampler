package org.mulinlab.variantsampler.utils.enumset;

import com.google.gson.Gson;
import org.mulinlab.variantsampler.utils.GP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TissueType {
    ADIPOSE_SUBCUTANEOUS(0, "Adipose Subcutaneous"),
    ADIPOSE_VISCERAL_OMENTUM(1, "Adipose Visceral Omentum"),
    ADRENAL_GLAND(2, "Adrenal Gland"),
    ARTERY_AORTA(3, "Artery Aorta"),
    ARTERY_CORONARY(4, "Artery Coronary"),
    ARTERY_TIBIAL(5, "Artery Tibial"),
    BRAIN_AMYGDALA(6, "Brain Amygdala"),
    BRAIN_ANTERIOR_CINGULATE_CORTEX_BA24(7, "Brain Anterior cingulate cortex BA24"),
    BRAIN_CAUDATE_BASAL_GANGLIA(8, "Brain Caudate basal ganglia"),
    BRAIN_CEREBELLAR_HEMISPHERE(9, "Brain Cerebellar Hemisphere"),
    BRAIN_CEREBELLUM(10, "Brain Cerebellum"),
    BRAIN_CORTEX(11, "Brain Cortex"),
    BRAIN_FRONTAL_CORTEX_BA9(12, "Brain Frontal Cortex BA9"),
    BRAIN_HIPPOCAMPUS(13, "Brain Hippocampus"),
    BRAIN_HYPOTHALAMUS(14, "Brain Hypothalamus"),
    BRAIN_NUCLEUS_ACCUMBENS_BASAL_GANGLIA(15, "Brain Nucleus accumbens basal ganglia"),
    BRAIN_PUTAMEN_BASAL_GANGLIA(16, "Brain Putamen basal ganglia"),
    BRAIN_SPINAL_CORD_CERVICAL_C1(17, "Brain Spinal cord cervical c"),
    BRAIN_SUBSTANTIA_NIGRA(18, "Brain Substantia nigra"),
    BREAST_MAMMARY_TISSUE(19, "Breast Mammary Tissue"),
    CELLS_CULTURED_FIBROBLASTS(20, "Cells Cultured fibroblasts"),
    CELLS_EBV_TRANSFORMED_LYMPHOCYTES(21, "Cells EBV-transformed lymphocytes"),
    COLON_SIGMOID(22, "Colon Sigmoid"),
    COLON_TRANSVERSE(23, "Colon Transverse"),
    ESOPHAGUS_GASTROESOPHAGEAL_JUNCTION(24, "Esophagus Gastroesophageal Junction"),
    ESOPHAGUS_MUCOSA(25, "Esophagus Mucosa"),
    ESOPHAGUS_MUSCULARIS(26, "Esophagus Muscularis"),
    HEART_ATRIAL_APPENDAGE(27, "Heart Atrial Appendage"),
    HEART_LEFT_VENTRICLE(28, "Heart Left Ventricle"),
    KIDNEY_CORTEX(29, "Kidney Cortex"),
    LIVER(30, "Liver"),
    LUNG(31, "Lung"),
    MINOR_SALIVARY_GLAND(32, "Minor Salivary Gland"),
    MUSCLE_SKELETAL(33, "Muscle Skeletal"),
    NERVE_TIBIAL(34, "Nerve Tibial"),
    OVARY(35, "Ovary"),
    PANCREAS(36, "Pancreas"),
    PITUITARY(37, "Pituitary"),
    PROSTATE(38, "Prostate"),
    SKIN_NOT_SUN_EXPOSED_SUPRAPUBIC(39, "Skin Not Sun Exposed Suprapubic"),
    SKIN_SUN_EXPOSED_LOWER_LEG(40, "Skin Sun Exposed Lower leg"),
    SMALL_INTESTINE_TERMINAL_ILEUM(41, "Small Intestine Terminal Ileum"),
    SPLEEN(42, "Spleen"),
    STOMACH(43, "Stomach"),
    TESTIS(44, "Testis"),
    THYROID(45, "Thyroid"),
    UTERUS(46, "Uterus"),
    VAGINA(47, "Vagina"),
    WHOLE_BLOOD(48, "Whole Blood");

    private final int idx;
    private final String name;

    TissueType(final int idx, final String name) {
        this.idx = idx;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TissueType getVal(final int index) {
        return  TissueType.values()[index];
    }

    public static int getIdx(final TissueType tissueType) {
        if (tissueType == null) {
            return GP.NO_TISSUE;
        } else {
            for (TissueType tissueType1:TissueType.values()) {
                if(tissueType == tissueType1) {
                    return tissueType1.idx;
                }
            }
            return GP.NO_TISSUE;
        }
    }

    public static String desc() {
        List<Map<String, Object>> list = new ArrayList<>();

        for (TissueType t: TissueType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", t.getName());
            map.put("id", t.idx);
            list.add(map);
        }

        return new Gson().toJson(list);
    }
}
