package org.robolectric.res;

import android.view.View;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;
import org.robolectric.shadows.RoboAttributeSet;
import org.robolectric.util.CustomView;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.util.TestUtil.SYSTEM_PACKAGE;
import static org.robolectric.util.TestUtil.TEST_PACKAGE;

@RunWith(TestRunners.WithDefaults.class)
public class RoboAttributeSetTest {

    private RoboAttributeSet roboAttributeSet;
    private ResourceLoader resourceLoader;

    @Before
    public void setUp() throws Exception {
        resourceLoader = Robolectric.getShadowApplication().getResourceLoader();
    }

    @Test
    public void getSystemAttributeResourceValue_shouldReturnTheResourceValue() throws Exception {
        createTestAttributeSet(new Attribute("android:attr/text", "@android:string/ok", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue("android", "text", 0)).isEqualTo(android.R.string.ok);
    }

    @Test
    public void getAttributeResourceValue_shouldReturnTheResourceValue() throws Exception {
        createTestAttributeSet(new Attribute("android:attr/text", "@string/ok", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue("android", "text", 0)).isEqualTo(R.string.ok);
    }

    @Test
    public void getSystemAttributeResourceValue_shouldNotReturnTheResourceValueIfNameSpaceDoesNotMatch() throws Exception {
        createTestAttributeSet(new Attribute("com.another.domain:attr/text", "@android:string/ok", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue("android", "text", 0)).isEqualTo(0);
    }

    @Test
    public void getAttributeResourceValue_shouldReturnTheResourceValueFromSystemNamespace() throws Exception {
        createTestAttributeSet(new Attribute("com.another.domain:attr/text", "@android:string/ok", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue("com.another.domain", "text", 0)).isEqualTo(android.R.string.ok);
    }

    @Test
    public void getSystemAttributeResourceValue_shouldReturnDefaultValueForNullResourceId() throws Exception {
        createTestAttributeSet(new Attribute("android:attr/text", "@null", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue("com.some.namespace", "text", 0)).isEqualTo(0);
    }

    @Test
    public void getSystemAttributeResourceValue_shouldReturnValueForMatchingNamespace() throws Exception {
        createTestAttributeSet(new Attribute("com.some.namespace:attr/id", "@id/burritos", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue("com.some.namespace", "id", 0)).isEqualTo(R.id.burritos);
    }

    @Test
    public void getSystemAttributeResourceValue_shouldReturnDefaultValueForNonMatchingNamespaceId() throws Exception {
        createTestAttributeSet(new Attribute("com.some.namespace:attr/id", "@id/burritos", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue("com.some.other.namespace", "id", 0)).isEqualTo(0);
    }

    @Test
    public void shouldCopeWithDefiningSystemIds() throws Exception {
        createTestAttributeSet(new Attribute("android:attr/id", "@+id/text1", SYSTEM_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue("android", "id", 0)).isEqualTo(android.R.id.text1);
    }

    @Test
    public void shouldCopeWithDefiningLocalIds() throws Exception {
        createTestAttributeSet(new Attribute("android:attr/id", "@+id/text1", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue("android", "id", 0)).isEqualTo(R.id.text1);
    }

    @Test
    public void getAttributeResourceValue_withNamespace_shouldReturnTheResourceValue() throws Exception {
        createTestAttributeSet(new Attribute(TEST_PACKAGE + ":attr/message", "@string/howdy", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue(TEST_PACKAGE, "message", 0)).isEqualTo(R.string.howdy);
    }

    @Test
    public void getAttributeResourceValue_shouldReturnDefaultValueWhenAttributeIsNull() throws Exception {
        createTestAttributeSet(new Attribute(TEST_PACKAGE + ":attr/message", "@null", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeResourceValue(TEST_PACKAGE, "message", -1)).isEqualTo(-1);
    }

    @Test
    public void getAttributeResourceValue_shouldReturnDefaultValueWhenNotInAttributeSet() throws Exception {
        createTestAttributeSet();
        assertThat(roboAttributeSet.getAttributeResourceValue(TEST_PACKAGE, "message", -1)).isEqualTo(-1);
    }

    @Test
    public void getAttributeBooleanValue_shouldGetBooleanValuesFromAttributes() throws Exception {
        createTestAttributeSet(new Attribute(TEST_PACKAGE + ":attr/isSugary", "true", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeBooleanValue(TEST_PACKAGE, "isSugary", false)).isTrue();
    }

    @Test
    public void getAttributeBooleanValue_withNamespace_shouldGetBooleanValuesFromAttributes() throws Exception {
        createTestAttributeSet(new Attribute("xxx:attr/isSugary", "true", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeBooleanValue("xxx", "isSugary", false)).isTrue();
    }

    @Test
    public void getAttributeBooleanValue_shouldReturnDefaultBooleanValueWhenNotInAttributeSet() throws Exception {
        createTestAttributeSet();
        assertThat(roboAttributeSet.getAttributeBooleanValue("com.some.namespace", "isSugary", true)).isTrue();
    }

    @Test
    public void getAttributeValue_byName_shouldReturnValueFromAttribute() throws Exception {
        createTestAttributeSet(new Attribute(TEST_PACKAGE + ":attr/isSugary", "oh heck yeah", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeValue(TEST_PACKAGE, "isSugary")).isEqualTo("oh heck yeah");
    }

    @Test
    public void getAttributeValue_byId_shouldReturnValueFromAttribute() throws Exception {
        createTestAttributeSet(new Attribute(TEST_PACKAGE + ":attr/isSugary", "oh heck yeah", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeValue(0)).isEqualTo("oh heck yeah");
    }

    @Test
    public void getAttributeIntValue_shouldReturnValueFromAttribute() throws Exception {
        roboAttributeSet = new RoboAttributeSet(asList(new Attribute(TEST_PACKAGE + ":attr/sugarinessPercent", "100", TEST_PACKAGE)),
                resourceLoader, null);
        assertThat(roboAttributeSet.getAttributeIntValue(TEST_PACKAGE, "sugarinessPercent", 0)).isEqualTo(100);
    }

    @Test
    public void getAttributeIntValue_shouldReturnHexValueFromAttribute() throws Exception {
        roboAttributeSet = new RoboAttributeSet(asList(new Attribute(TEST_PACKAGE + ":attr/sugarinessPercent", "0x10", TEST_PACKAGE)),
                resourceLoader, null);
        assertThat(roboAttributeSet.getAttributeIntValue(TEST_PACKAGE, "sugarinessPercent", 0)).isEqualTo(16);
    }

    @Test
    public void getAttributeIntValue_shouldReturnStyledValueFromAttribute() throws Exception {
        roboAttributeSet = new RoboAttributeSet(asList(
                new Attribute(TEST_PACKAGE + ":attr/gravity", "center|fill_vertical", TEST_PACKAGE),
                new Attribute("android:attr/orientation", "vertical", TEST_PACKAGE)
        ), resourceLoader, CustomView.class);
        assertThat(roboAttributeSet.getAttributeIntValue(TEST_PACKAGE, "gravity", 0)).isEqualTo(0x11 | 0x70);
        assertThat(roboAttributeSet.getAttributeIntValue("android", "orientation", -1)).isEqualTo(1); // style from LinearLayout
    }

    @Ignore
    @Test
    public void getAttributeIntValue_shouldNotReturnStyledValueFromAttributeForSuperclass() throws Exception {
        roboAttributeSet = new RoboAttributeSet(asList(new Attribute(TEST_PACKAGE + ":attr/gravity", "center|fill_vertical", TEST_PACKAGE)),
                resourceLoader, View.class);
        assertThat(roboAttributeSet.getAttributeIntValue(TEST_PACKAGE, "gravity", 0)).isEqualTo(0); // todo: what do we expect here?
    }

    @Test
    public void getAttributeIntValue_shouldReturnEnumValuesForEnumAttributes() throws Exception {
        roboAttributeSet = new RoboAttributeSet(asList(new Attribute(TEST_PACKAGE + ":attr/itemType", "ungulate", TEST_PACKAGE)),
                resourceLoader, CustomView.class);
        assertThat(roboAttributeSet.getAttributeIntValue(TEST_PACKAGE, "itemType", 0)).isEqualTo(1);
    }

    @Test
    public void getAttributeValue_shouldReturnAttributeAssociatedWithResourceId() throws Exception {
        createTestAttributeSet(new Attribute("ns:attr/textStyle2", "expected value", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeValue(0)).isEqualTo("expected value");
    }

    @Test
    public void getAttributeValue_shouldReturnNullIfNoAttributeSet() throws Exception {
        createTestAttributeSet();
        int nonExistantResource = 12345;
        assertThat(roboAttributeSet.getAttributeValue(nonExistantResource)).isNull();
    }

    @Test
    public void getAttributeIntValue_shouldReturnValueFromAttributeWhenNotInAttributeSet() throws Exception {
        createTestAttributeSet();
        assertThat(roboAttributeSet.getAttributeIntValue("some namespace", "sugarinessPercent", 42)).isEqualTo(42);
    }

    @Test
    public void getAttributeIntValue_shouldReturnEnumValuesForEnumAttributesWhenNotInAttributeSet() throws Exception {
        createTestAttributeSet();
        assertThat(roboAttributeSet.getAttributeIntValue("some namespace", "itemType", 24)).isEqualTo(24);
    }

    @Test
    public void getAttributeFloatValue_shouldGetFloatValuesFromAttributes() throws Exception {
        createTestAttributeSet(new Attribute(TEST_PACKAGE + ":attr/sugaryScale", "1234.456", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeFloatValue(TEST_PACKAGE, "sugaryScale", 78.9f)).isEqualTo(1234.456f);
    }

    @Test
    public void getAttributeFloatValue_withNamespace_shouldGetFloatValuesFromAttributes() throws Exception {
        createTestAttributeSet(new Attribute("xxx:attr/sugaryScale", "1234.456", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeFloatValue("xxx", "sugaryScale", 78.9f)).isEqualTo(1234.456f);
    }

    @Test
    public void getAttributeFloatValue_shouldReturnDefaultFloatValueWhenNotInAttributeSet() throws Exception {
        createTestAttributeSet();
        assertThat(roboAttributeSet.getAttributeFloatValue(TEST_PACKAGE, "sugaryScale", 78.9f)).isEqualTo(78.9f);
    }
    
    @Test
    public void getStyleAttribute_doesNotThrowException() throws Exception {
        createTestAttributeSet();
        roboAttributeSet.getStyleAttribute();
    }

    @Test
    public void getStyleAttribute_returnsZeroWhenNoStyle() throws Exception {
        createTestAttributeSet();
        assertThat(roboAttributeSet.getStyleAttribute()).isEqualTo(0);
    }

    @Test
    public void getStyleAttribute_returnsCorrectValue() throws Exception {
        createTestAttributeSet(new Attribute(":attr/style", "@style/FancyStyle", TEST_PACKAGE));
        assertThat(roboAttributeSet.getStyleAttribute()).isEqualTo(R.style.FancyStyle);
    }

    @Test
    public void getStyleAttribute_doesNotThrowException_whenStyleIsBogus() throws Exception {
        createTestAttributeSet(new Attribute(":attr/style", "@style/bogus_style", TEST_PACKAGE));
        assertThat(roboAttributeSet.getStyleAttribute()).isEqualTo(0);
    }

    @Test public void shouldConsiderSameNamedAttrsFromLibrariesEquivalent() throws Exception {
        createTestAttributeSet(new Attribute("org.robolectric.lib1:attr/offsetX", "1", TEST_PACKAGE));
        assertThat(roboAttributeSet.getAttributeValue("org.robolectric.lib1", "offsetX")).isEqualTo("1");
        assertThat(roboAttributeSet.getAttributeValue("org.robolectric.lib2", "offsetX")).isEqualTo("1");
    }

    @Test public void getAttributeNameResource() throws Exception {
        createTestAttributeSet(
                new Attribute("org.robolectric.lib1:attr/message", "1", TEST_PACKAGE),
                new Attribute("org.robolectric.lib1:attr/keycode", "1", TEST_PACKAGE)
                );
        assertThat(roboAttributeSet.getAttributeNameResource(0)).isEqualTo(0); // no id for attr.message for some reason...
        assertThat(roboAttributeSet.getAttributeNameResource(1)).isEqualTo(R.attr.keycode);
    }

    private void createTestAttributeSet(Attribute... attributes) {
        roboAttributeSet = new RoboAttributeSet(asList(attributes), resourceLoader, null);
    }

}
