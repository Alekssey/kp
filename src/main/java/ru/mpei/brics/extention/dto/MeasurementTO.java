package ru.mpei.brics.extention.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MeasurementTO {
    private String parameterName;
    private String time;
    private String value;


    public MeasurementTO(String parameterName) {
        this.parameterName = parameterName;
    }

    public MeasurementTO(String parameterName, String value) {
        this.parameterName = parameterName;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                " parameterName='" + parameterName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasurementTO meas = (MeasurementTO) o;
        return Objects.equals(parameterName, meas.parameterName);
    }

    @Override
    public int hashCode() {
        int result = 31 * (parameterName != null ? parameterName.hashCode() : 0);
        return result;
    }
}
