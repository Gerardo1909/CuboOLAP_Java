import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomGroupBy {

    public static Map<List<String>, Integer> customGroupBy(List<Map<String, Object>> data, List<String> groupCols) {
        Map<List<String>, List<Integer>> groups = new HashMap<>();

        // Iterar sobre los datos y agruparlos
        for (Map<String, Object> row : data) {
            List<String> key = new ArrayList<>();
            for (String col : groupCols) {
                key.add((String) row.get(col));
            }
            if (!groups.containsKey(key)) {
                groups.put(key, new ArrayList<>());
            }
            groups.get(key).add((Integer) row.get("Ventas"));
        }

        // Calcular agregaciones dentro de cada grupo
        Map<List<String>, Integer> result = new HashMap<>();
        for (Map.Entry<List<String>, List<Integer>> entry : groups.entrySet()) {
            List<Integer> groupData = entry.getValue();
            int sum = 0;
            for (int value : groupData) {
                sum += value;
            }
            result.put(entry.getKey(), sum);
        }

        return result;
    }

    public static void main(String[] args) {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row1 = Map.of("Tiempo", "2023-01-01", "Producto", "A", "Ventas", 100);
        Map<String, Object> row2 = Map.of("Tiempo", "2023-01-01", "Producto", "B", "Ventas", 120);
        Map<String, Object> row3 = Map.of("Tiempo", "2023-01-02", "Producto", "A", "Ventas", 330);
        Map<String, Object> row4 = Map.of("Tiempo", "2023-01-03", "Producto", "B", "Ventas", 420);
        data.add(row1);
        data.add(row2);
        data.add(row3);
        data.add(row4);

        List<String> groupCols = List.of("Tiempo");
        Map<List<String>, Integer> groupedData = customGroupBy(data, groupCols);

        // Formatear la salida
        for (Map.Entry<List<String>, Integer> entry : groupedData.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}