package com.mj.drinkmorewater;

import com.mj.drinkmorewater.Utils.DateUtils;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the DateUtils logic.
 */
public class DateUtilsTest {

    @Test
    public void currentLocalDate_ReturnsTrue() {
        LocalDate localDate = DateUtils.getCurrentDate();
        LocalDate currentLocalDate = LocalDate.now();

        assertEquals(currentLocalDate, localDate);
    }

}
