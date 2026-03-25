package com.star.pivot.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * 分层依赖约束：framework 不依赖业务模块；业务模块不依赖 controller 与 controller 模块下的 config。
 */
@AnalyzeClasses(packages = "com.star.pivot", importOptions = ImportOption.DoNotIncludeTests.class)
class StarPivotArchitectureTest {

    private static final String[] FRAMEWORK = {"com.star.pivot.framework..", "com.star.pivot.security.."};

    private static final String[] MODULE = {
        "com.star.pivot.system..",
        "com.star.pivot.quartz..",
        "com.star.pivot.generator..",
        "com.star.pivot.dict..",
        "com.star.pivot.monitor.."
    };

    @ArchTest
    static final ArchRule framework_must_not_depend_on_business_modules =
            noClasses().that().resideInAnyPackage(FRAMEWORK).should().dependOnClassesThat().resideInAnyPackage(MODULE);

    @ArchTest
    static final ArchRule business_modules_must_not_depend_on_web_entry =
            noClasses()
                    .that()
                    .resideInAnyPackage(MODULE)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("com.star.pivot.controller..", "com.star.pivot.config..");
}
