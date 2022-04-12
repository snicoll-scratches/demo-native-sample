package org.springframework.boot.autoconfigure;

import java.lang.reflect.Executable;
import java.util.function.Supplier;

import org.springframework.aot.generator.CodeContribution;
import org.springframework.aot.generator.DefaultCodeContribution;
import org.springframework.aot.generator.ProtectedAccess.Options;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.factory.generator.BeanFactoryContribution;
import org.springframework.beans.factory.generator.BeanInstantiationGenerator;
import org.springframework.beans.factory.generator.BeanParameterGenerator;
import org.springframework.beans.factory.generator.BeanRegistrationBeanFactoryContribution;
import org.springframework.beans.factory.generator.BeanRegistrationContributionProvider;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages.BasePackages;

/**
 *
 * @author Stephane Nicoll
 */
class BasePackagesBeanRegistrationContributionProvider implements BeanRegistrationContributionProvider {

	@Override
	public BeanFactoryContribution getContributionFor(String beanName, RootBeanDefinition beanDefinition) {
		if (BasePackages.class.getName().equals(beanDefinition.getBeanClassName())) {
			String[] packageNames = getPackageNames(beanDefinition);
			return new BeanRegistrationBeanFactoryContribution(beanName, beanDefinition,
					getBeanInstantiationGenerator(packageNames)) {
				@Override
				protected boolean shouldDeclareCreator(Executable instanceCreator) {
					return false;
				}
			};
		}
		return null;
	}

	BeanInstantiationGenerator getBeanInstantiationGenerator(String[] packagesToScan) {
		return new BeanInstantiationGenerator() {
			@Override
			public Executable getInstanceCreator() {
				return BasePackages.class.getDeclaredConstructors()[0];
			}

			@Override
			public CodeContribution generateBeanInstantiation(RuntimeHints runtimeHints) {
				CodeContribution codeContribution = new DefaultCodeContribution(runtimeHints);
				codeContribution.protectedAccess().analyze(getInstanceCreator(), Options.defaults().build());
				codeContribution.statements().addStatement("() -> new $T($L)", BasePackages.class,
						BeanParameterGenerator.INSTANCE.generateParameterValue(packagesToScan));
				return codeContribution;
			}
		};
	}

	private String[] getPackageNames(RootBeanDefinition beanDefinition) {
		Supplier<?> instanceSupplier = beanDefinition.getInstanceSupplier();
		BasePackages basePackages = (BasePackages) instanceSupplier.get();
		return basePackages.get().toArray(new String[0]);
	}

}
