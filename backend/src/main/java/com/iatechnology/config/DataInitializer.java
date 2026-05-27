package com.iatechnology.config;

import com.iatechnology.model.*;
import com.iatechnology.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepo,
                               DomainRepository domainRepo,
                               ResearcherRepository researcherRepo,
                               PublicationRepository pubRepo,
                               NewsRepository newsRepo,
                               PasswordEncoder encoder,
                               Environment env) {
        return args -> {
            if (!"true".equalsIgnoreCase(env.getProperty("app.seed.enabled", "true"))) return;
            if (userRepo.count() > 0) return;

            // ── Users ──
            var admin = user(userRepo, encoder, "Admin User", "admin@ia-technology.com", "admin123", Role.ADMIN, true);
            var mod = user(userRepo, encoder, "Moderator", "moderator@ia-technology.com", "moderator123", Role.MODERATEUR, true);
            var franck = user(userRepo, encoder, "Franck Duval", "franck@example.com", "user123", Role.MODERATEUR, true);
            var alice = user(userRepo, encoder, "Alice Martin", "alice@example.com", "user123", Role.UTILISATEUR, true);
            var bob = user(userRepo, encoder, "Bob Durand", "bob@example.com", "user123", Role.UTILISATEUR, true);
            var clara = user(userRepo, encoder, "Clara Dupont", "clara@example.com", "user123", Role.UTILISATEUR, true);
            var david = user(userRepo, encoder, "David Moreau", "david@example.com", "user123", Role.UTILISATEUR, false);
            var erin = user(userRepo, encoder, "Erin Petit", "erin@example.com", "user123", Role.UTILISATEUR, true);

            // ── Domains ──
            var nlp = domain(domainRepo, "Traitement du Langage Naturel", "Exploration des architectures Transformer, analyse semantique et modeles de langage a grande echelle (LLM).", "TAXONOMIE ALPHA");
            var cv = domain(domainRepo, "Vision par Ordinateur", "Recherche sur la reconnaissance de formes, segmentation d'images et traitement video en temps reel via Deep Learning.", "PERCEPTION");
            var cyber = domain(domainRepo, "Cybersecurite IA", "Developpement d'algorithmes de detection d'intrusions, analyse de menaces et systemes de defense automatises.", "GOUVERNANCE");
            var robotics = domain(domainRepo, "Robotique Autonome", "Algorithmes de navigation, perception 3D et interaction homme-machine dans des environnements dynamiques.", "SYSTEMES PHYSIQUES");
            var ethics = domain(domainRepo, "Ethique & IA", "Analyse des biais algorithmiques, transparence des systemes et cadres reglementaires pour une IA responsable.", "GOUVERNANCE");
            var bioinfo = domain(domainRepo, "Bio-informatique", "Application du machine learning au sequencage genetique, repliement des proteines et decouverte de medicaments.", "SCIENCES DE LA VIE");
            var quantum = domain(domainRepo, "Informatique Quantique", "Hybridation du Quantum Computing et de l'IA pour resoudre les problemes d'optimisation complexes.", "FUTUR CALCUL");
            var emerging = domain(domainRepo, "Domaines Emergents", "Recherche exploratoire : IA generative, systemes multi-agents et IA embarquee.", "FUTUR CALCUL");

            // ── Researchers ──
            var r1 = researcher(researcherRepo, "Dhia", "Selmi", "dhia.selmi@ia.tn", "Specialiste en NLP et architectures Transformer", nlp);
            var r2 = researcher(researcherRepo, "Sarah", "Chen", "sarah.chen@ia.tn", "Experte en segmentation d'images medicales", cv);
            var r3 = researcher(researcherRepo, "Marc", "Lefebvre", "marc.lefebvre@ia.tn", "Chercheur en robotique cognitive et navigation autonome", robotics);
            var r4 = researcher(researcherRepo, "Amina", "Jallow", "amina.jallow@ia.tn", "Chercheuse en ethique algorithmique et biais des modeles", ethics);
            var r5 = researcher(researcherRepo, "Kenji", "Sato", "kenji.sato@ia.tn", "Expert en algorithmes quantiques et optimisation", quantum);
            var r6 = researcher(researcherRepo, "Elena", "Rodriguez", "elena.rodriguez@ia.tn", "Specialiste en cybersecurite et detection d'intrusions", cyber);
            var r7 = researcher(researcherRepo, "Mehdi", "Ayadi", "mehdi.ayadi@ia.tn", "Chercheur en apprentissage federe et confidentialite", nlp);
            var r8 = researcher(researcherRepo, "Sophie", "Laurent", "sophie.laurent@ia.tn", "Experte en bio-informatique computationnelle", bioinfo);
            var r9 = researcher(researcherRepo, "Thomas", "Muller", "thomas.muller@ia.tn", "Chercheur en vision 3D et reconstruction", cv);
            var r10 = researcher(researcherRepo, "Imen", "Gharbi", "imen.gharbi@ia.tn", "Specialiste en IA generative et modeles de diffusion", emerging);
            var r11 = researcher(researcherRepo, "Lucas", "Bernard", "lucas.bernard@ia.tn", "Expert en systemes multi-agents", emerging);
            var r12 = researcher(researcherRepo, "Fatima", "Zahra", "fatima.zahra@ia.tn", "Chercheuse en traitement automatique de l'arabe", nlp);

            // ── Publications with rich abstracts ──
            LocalDateTime now = LocalDateTime.now();

            pub(pubRepo, "Scalable Topological Qubits for Fault-Tolerant Computing Architectures", """
                This research introduces a novel framework for braiding Majorana fermions in hybrid semiconductor-superconductor nanowires, \
                demonstrating a 40% reduction in decoherence rates compared to existing topological quantum computing approaches. \
                We develop a scalable architecture that combines topological protection with surface code error correction, achieving logical error rates below 10^-6 per operation cycle. \
                Our experimental results on a 12-qubit prototype show that braiding operations can be performed with 99.7% fidelity in under 100 nanoseconds. \
                The key innovation lies in a new geometric configuration of the nanowire network that minimizes quasiparticle poisoning while maintaining the topological gap. \
                We further demonstrate that our architecture can be scaled to 100+ qubits using standard semiconductor fabrication techniques, \
                making it a viable path toward practical fault-tolerant quantum computation for optimization and cryptography applications.""",
                    r5, quantum, now.minusMonths(17), false);

            pub(pubRepo, "Deep Learning Pipelines for Real-Time Medical Image Segmentation", """
                We present a multi-scale U-Net architecture optimized for real-time segmentation of pathological tissue in MRI and CT scans, \
                achieving a 94.2% Dice score on the BraTS 2024 benchmark for brain tumor segmentation. \
                Our approach introduces a novel attention-gated skip connection mechanism that selectively focuses on the most diagnostically relevant features at each resolution level. \
                The model processes 512x512 images in 18 milliseconds on a single NVIDIA A100 GPU, enabling real-time intraoperative guidance. \
                We validate our pipeline on three clinical datasets encompassing brain tumors, liver lesions, and cardiac pathologies, \
                demonstrating consistent improvements of 3-7% Dice score over existing architectures including nnU-Net, TransUNet, and Swin-UNETR. \
                A key contribution is our adaptive loss function that dynamically balances boundary precision and region coverage based on lesion size, \
                which is particularly effective for small metastatic lesions that are often missed by standard segmentation approaches. \
                Clinical evaluation by five board-certified radiologists confirms that our segmentation masks are clinically acceptable in 97% of cases.""",
                    r2, cv, now.minusMonths(16), false);

            pub(pubRepo, "Side-Channel Attacks on Post-Quantum Cryptographic Implementations", """
                This paper presents a comprehensive analysis of timing and power side-channel vulnerabilities in lattice-based encryption schemes, \
                specifically targeting CRYSTALS-Kyber and CRYSTALS-Dilithium implementations deployed on ARM Cortex-M4 embedded processors. \
                We demonstrate three novel attack vectors: a cache-timing attack that recovers the secret key in under 10,000 traces, \
                a differential power analysis attack effective against masked implementations with up to second-order protection, \
                and a combined electromagnetic-timing attack that exploits the Number Theoretic Transform (NTT) butterfly operations. \
                Our countermeasures include a constant-time NTT implementation with randomized execution order, \
                a shuffled sampling procedure for the binomial distribution, and hardware-backed masking using the ARM TrustZone CryptoCell. \
                We evaluate the performance overhead of each countermeasure and show that full protection can be achieved with only a 23% increase in computation time. \
                These findings are critical for the secure deployment of post-quantum cryptography in IoT devices and embedded systems \
                as NIST transitions to quantum-resistant standards.""",
                    r6, cyber, now.minusMonths(15), false);

            pub(pubRepo, "Attention Mechanisms in Low-Resource Language Translation", """
                We propose novel cross-lingual transfer approaches using lightweight adapter modules for machine translation involving under-resourced African and Asian languages. \
                Our method, AdapterNMT, inserts language-specific adapter layers into a pre-trained mBART-50 model, \
                requiring only 2% of the original parameters to be fine-tuned per new language pair. \
                We evaluate on 12 low-resource language pairs including Wolof-French, Bambara-English, Khmer-English, and Yoruba-French, \
                achieving BLEU score improvements of 5.3 to 11.7 points over direct fine-tuning baselines. \
                The adapter architecture uses a bottleneck design with language-specific layer normalization and a novel attention redistribution mechanism \
                that learns to reweight the pre-trained attention patterns based on the syntactic structure of the target language. \
                We also introduce a curriculum learning strategy that progressively increases translation difficulty, \
                starting with high-resource related language pairs before transitioning to the target low-resource pair. \
                Our approach makes high-quality neural machine translation accessible for languages with as few as 50,000 parallel sentences.""",
                    r1, nlp, now.minusMonths(14), false);

            pub(pubRepo, "Autonomous Navigation in Unstructured Outdoor Environments", """
                We present a terrain-adaptive motion planning algorithm that combines LiDAR-inertial odometry with learned traversability cost maps \
                for autonomous robot navigation in challenging outdoor environments including forests, construction sites, and disaster zones. \
                Our approach uses a self-supervised learning framework where the robot learns traversability from its own proprioceptive feedback \
                during initial exploration, eliminating the need for manually labeled terrain data. \
                The system integrates a 3D LiDAR point cloud with IMU data through a tightly-coupled factor graph optimization, \
                achieving localization accuracy of 0.3% over distances exceeding 5 kilometers in GPS-denied environments. \
                We introduce a hierarchical planning architecture with a global topological planner operating at 2 Hz \
                and a local reactive planner at 20 Hz that handles dynamic obstacle avoidance. \
                Field tests on a Boston Dynamics Spot robot across 50 km of diverse terrain demonstrate a 94% autonomous completion rate \
                with zero collisions, compared to 71% for the baseline DWA planner. \
                The learned traversability model generalizes across seasons and weather conditions with only 30 minutes of adaptation data.""",
                    r3, robotics, now.minusMonths(13), false);

            pub(pubRepo, "Algorithmic Fairness Auditing in Criminal Justice Systems", """
                This comprehensive study analyzes racial and socioeconomic biases in recidivism prediction algorithms currently deployed across 12 US states, \
                examining over 2.3 million case records spanning 2015-2024. \
                We develop a multi-dimensional fairness audit framework that evaluates algorithms along seven axes: demographic parity, equalized odds, \
                calibration, predictive parity, individual fairness, counterfactual fairness, and intersectional equity. \
                Our analysis reveals that even algorithms satisfying standard demographic parity constraints exhibit significant disparities \
                at the intersection of race and socioeconomic status, with false positive rates up to 2.7 times higher for Black defendants from low-income zip codes. \
                We propose a novel post-processing technique called Intersectional Calibration that adjusts prediction thresholds for each demographic subgroup \
                while maintaining overall predictive accuracy within 1.5% of the unconstrained model. \
                The study also examines the feedback loop between algorithmic predictions and sentencing decisions, \
                showing that biased predictions can amplify existing disparities over time through a reinforcement effect. \
                We provide an open-source auditing toolkit and recommend specific policy interventions for legislatures considering algorithmic accountability laws.""",
                    r4, ethics, now.minusMonths(12), false);

            pub(pubRepo, "Federated Learning for Privacy-Preserving Healthcare Analytics", """
                We present a differentially private federated learning framework enabling multi-hospital collaboration on clinical prediction tasks \
                without sharing individual patient records. Our system, FedHealth-DP, combines secure aggregation with local differential privacy \
                and achieves an epsilon-delta guarantee of (1.2, 10^-5) while maintaining model utility within 3% of centralized training. \
                We deploy the framework across 8 hospitals in a real-world pilot study for predicting 30-day hospital readmission, \
                training on a combined dataset of 1.2 million patient encounters without any raw data leaving the hospital network. \
                The key technical innovation is an adaptive clipping mechanism that adjusts the gradient norm bound per hospital based on data heterogeneity, \
                reducing the accuracy gap between federated and centralized models from 8.2% to 2.7%. \
                We also introduce a communication-efficient compression scheme that reduces bandwidth requirements by 94% through top-k sparsification \
                combined with error feedback. Our framework handles the statistical challenge of non-IID data distributions across hospitals \
                through a personalization layer that adapts the global model to each institution's patient demographics. \
                HIPAA compliance is verified through a formal privacy analysis and penetration testing by independent auditors.""",
                    r7, nlp, now.minusMonths(11), false);

            pub(pubRepo, "Protein Folding Prediction using Graph Neural Networks", """
                We introduce ProtGNN, a novel graph neural network architecture achieving state-of-the-art accuracy on CASP15 targets \
                for protein tertiary structure prediction. Our approach represents proteins as geometric graphs where nodes correspond to amino acid residues \
                and edges encode both sequential proximity and predicted inter-residue contacts from multiple sequence alignments. \
                ProtGNN uses an SE(3)-equivariant message-passing scheme that respects the rotational and translational symmetry of 3D protein structures, \
                combined with a multi-scale pooling operator that captures both local secondary structure motifs and global fold topology. \
                On the CASP15 free-modeling targets, ProtGNN achieves a mean GDT-TS score of 78.3, compared to 72.1 for RoseTTAFold and 82.5 for AlphaFold2, \
                while requiring only 1/10th of the computational resources during inference. \
                A key contribution is our residue-level confidence estimation module that predicts lDDT scores with a Pearson correlation of 0.89, \
                enabling researchers to identify reliable regions of predicted structures. \
                We validate our predictions experimentally through X-ray crystallography on 5 novel protein targets, \
                confirming RMSD values below 2.1 Angstroms for all cases. \
                The model also excels at predicting the effects of point mutations on protein stability, achieving AUC of 0.91 on the ProTherm dataset.""",
                    r8, bioinfo, now.minusMonths(10), false);

            pub(pubRepo, "Real-Time 3D Object Detection from LiDAR Point Clouds", """
                We present VoxelDet, an efficient voxel-based 3D object detection network capable of processing 100,000 LiDAR points in under 20 milliseconds \
                on an NVIDIA Jetson Orin platform for autonomous driving applications. \
                Our architecture introduces sparse dynamic voxelization that adaptively adjusts voxel resolution based on object density, \
                using finer voxels in cluttered urban areas and coarser voxels in open highway scenes. \
                The detection head employs a center-point heuristic with IoU-aware confidence scoring that eliminates the need for non-maximum suppression, \
                reducing post-processing latency by 60%. On the nuScenes validation set, VoxelDet achieves 68.7 mAP and 72.1 NDS \
                while running at 52 FPS on the Jetson platform, outperforming CenterPoint by 2.3 mAP at 3x the speed. \
                We introduce a novel temporal fusion module that aggregates information from the previous 5 frames using ego-motion compensation, \
                improving detection of slow-moving and stationary objects by 12.4 mAP. \
                The system is validated in a Level 4 autonomous vehicle test fleet over 50,000 km of urban and highway driving, \
                achieving zero false-negative detections for pedestrians within 30 meters at a false-positive rate below 0.1 per kilometer.""",
                    r9, cv, now.minusMonths(9), false);

            pub(pubRepo, "Multi-Agent Reinforcement Learning for Swarm Robotics", """
                We develop decentralized coordination protocols enabling 50+ autonomous drones to collaborate in search-and-rescue scenarios \
                using multi-agent reinforcement learning with communication. Our framework, SwarmRL, uses a graph attention network \
                for inter-agent communication that scales linearly with the number of agents, avoiding the exponential complexity of centralized approaches. \
                Each drone learns a policy that maps its local observations and received messages to navigation actions and outgoing messages, \
                trained using a novel curriculum that progressively increases the number of agents and environment complexity. \
                In simulated disaster environments with building collapse, flooding, and fire scenarios, \
                SwarmRL locates 95% of simulated victims within 12 minutes using 50 drones, compared to 67% for a baseline frontier exploration strategy. \
                The communication protocol learns to share victim location estimates, hazard warnings, and coverage maps using only 32-byte messages per step. \
                We demonstrate zero-shot transfer from simulation to real hardware using 8 DJI Tello drones in an indoor testbed, \
                achieving 89% of the simulated performance without any fine-tuning. \
                Key robustness properties include graceful degradation under communication dropout (maintaining 80% performance with 30% message loss) \
                and dynamic re-tasking capability when new drones join or leave the swarm during a mission.""",
                    r11, emerging, now.minusMonths(8), false);

            pub(pubRepo, "Formal Verification Methods for Neural Network Safety", """
                We present automated verification tools for proving robustness bounds on safety-critical neural network controllers \
                used in autonomous vehicles, aircraft autopilots, and medical devices. \
                Our approach combines abstract interpretation with mixed-integer linear programming to provide certified guarantees \
                that a neural network controller will never produce unsafe actions within a formally specified input domain. \
                For a 6-layer ReLU network controlling aircraft collision avoidance (ACAS Xu benchmark), \
                our tool verifies all 45 safety properties in under 3 minutes, compared to 47 minutes for Marabou and 12 minutes for alpha-beta-CROWN. \
                We introduce a novel branch-and-bound strategy guided by gradient-based sensitivity analysis \
                that prioritizes the most critical neurons for case splitting, reducing the number of explored branches by 85%. \
                The verification results are encoded as machine-checkable proofs in the Lean theorem prover, \
                providing an additional layer of trust for certification authorities. \
                We apply our framework to verify a reinforcement-learned lane-keeping controller for autonomous vehicles, \
                proving that steering angle deviations remain below 5 degrees for all perturbations within the sensor noise envelope. \
                This represents the first formal safety guarantee for a neural network controller deployed in a production autonomous driving system.""",
                    r3, robotics, now.minusMonths(8), false);

            pub(pubRepo, "Lattice-Based Cryptography for IoT Constrained Devices", """
                We present a lightweight implementation of the CRYSTALS-Kyber key encapsulation mechanism \
                optimized for ARM Cortex-M4 processors with as little as 64 KB of RAM, \
                achieving sub-millisecond key exchange operations suitable for battery-powered IoT devices. \
                Our optimization strategy targets three levels: algorithmic (using the Number Theoretic Transform with merged layers), \
                implementation (assembly-optimized polynomial arithmetic with SIMD instructions), \
                and protocol (a session resumption mechanism that amortizes the cost of key establishment over multiple messages). \
                Benchmarks on the STM32L4 platform show key generation in 0.42 ms, encapsulation in 0.51 ms, and decapsulation in 0.48 ms, \
                representing a 3.2x speedup over the reference implementation while consuming 35% less energy per operation. \
                We also implement CRYSTALS-Dilithium digital signatures with similar optimizations, \
                enabling complete post-quantum TLS handshakes in under 8 milliseconds. \
                Security analysis includes resistance to side-channel attacks through constant-time execution and first-order masking. \
                Field deployment in a smart grid pilot project with 1,200 IoT sensors demonstrates seamless integration \
                with existing MQTT protocols and less than 2% increase in communication overhead compared to classical ECDH key exchange.""",
                    r6, cyber, now.minusMonths(7), false);

            pub(pubRepo, "Arabic Dialect Identification using Transformer Embeddings", """
                We present a fine-tuned AraBERT model capable of distinguishing 25 Arabic dialects with 91.3% accuracy on the NADI 2024 benchmark, \
                representing a 4.7% improvement over the previous state-of-the-art. \
                Our approach introduces a hierarchical classification strategy that first identifies the macro-region (Maghreb, Mashreq, Gulf, Nile Basin) \
                before fine-grained dialect classification, leveraging the linguistic hierarchy of Arabic dialectal variation. \
                We augment the training data using a novel back-translation pipeline through Modern Standard Arabic (MSA) as a pivot, \
                generating 500,000 synthetic dialect-labeled sentences that improve model robustness to code-switching and MSA interference. \
                The model architecture adds a dialect-aware attention layer on top of AraBERT that learns to focus on dialectally discriminative features \
                such as negation particles, pronoun clitics, and characteristic lexical items. \
                Error analysis reveals that the main confusion patterns align with known dialect continua, \
                particularly between Tunisian and Eastern Algerian Arabic, and between Gulf and Iraqi Arabic. \
                We release our trained model, augmented dataset, and a web API for dialect identification that processes text in under 50 milliseconds. \
                Applications include dialect-aware machine translation, social media analysis for public health surveillance, \
                and linguistic documentation of endangered dialectal varieties.""",
                    r12, nlp, now.minusMonths(6), false);

            pub(pubRepo, "Quantum Approximate Optimization for Supply Chain Logistics", """
                We demonstrate the application of the Quantum Approximate Optimization Algorithm (QAOA) on IBM's 127-qubit Eagle processor \
                for solving vehicle routing problems with time windows (VRPTW) relevant to last-mile delivery logistics. \
                Our hybrid quantum-classical approach uses a problem-specific ansatz that encodes route constraints directly into the quantum circuit, \
                reducing the required circuit depth by 60% compared to generic QAOA implementations. \
                On benchmark instances with up to 20 delivery locations, our quantum solution achieves route costs within 5% of the classical optimum \
                found by Google OR-Tools, while exploring the solution space 15% more efficiently than simulated annealing. \
                We introduce a noise-aware parameter optimization strategy using simultaneous perturbation stochastic approximation (SPSA) \
                that maintains solution quality on noisy quantum hardware with error rates up to 1%. \
                For larger instances (50-100 locations), we develop a quantum-classical decomposition that partitions the problem into sub-problems \
                solvable on current quantum hardware, achieving a 15% improvement over purely classical heuristics for instances with tight time windows. \
                Economic analysis for a major European logistics provider suggests potential annual savings of 8-12% on fuel costs \
                once quantum hardware with 1,000+ qubits becomes available, projected for 2027-2028.""",
                    r5, quantum, now.minusMonths(5), true);

            pub(pubRepo, "Explainable AI for Regulatory Compliance in Financial Services", """
                We develop a comprehensive framework for generating human-readable justifications for automated credit scoring decisions, \
                designed to meet the transparency requirements of the EU AI Act and the US Equal Credit Opportunity Act (ECOA). \
                Our approach combines SHAP (SHapley Additive exPlanations) values with a natural language generation module \
                that translates feature attributions into grammatically correct, legally compliant adverse action notices. \
                The system generates explanations at three levels of detail: a one-sentence summary for the applicant, \
                a paragraph-level explanation for customer service representatives, and a full technical report for compliance officers. \
                Evaluation by 15 consumer finance attorneys confirms that 94% of generated explanations meet regulatory requirements \
                without modification, compared to 61% for template-based approaches and 78% for raw SHAP value displays. \
                We also introduce a fairness-constrained explanation module that ensures explanations do not inadvertently reveal protected characteristics \
                or create proxy discrimination through correlated features. \
                The framework is integrated with a major European bank's credit decisioning pipeline, processing 50,000 applications daily \
                with an average explanation generation time of 200 milliseconds per application. \
                A controlled user study with 200 loan applicants shows that our explanations increase trust in the decision by 34% \
                and reduce complaint rates by 28% compared to standard rejection letters.""",
                    r4, ethics, now.minusMonths(5), true);

            pub(pubRepo, "LLM Fine-Tuning for Legal Document Analysis and Contract Review", """
                We present LegalLLM, a domain-adapted large language model for automated contract clause extraction, risk assessment, and compliance checking, \
                achieving 96.8% F1 score on the CUAD (Contract Understanding Atticus Dataset) benchmark. \
                Our fine-tuning strategy uses a three-stage approach: continued pre-training on 2 million legal documents from SEC filings and court opinions, \
                instruction tuning on 50,000 expert-annotated contract clauses, and reinforcement learning from human feedback (RLHF) with practicing attorneys. \
                The model identifies 41 distinct clause types including indemnification, limitation of liability, intellectual property assignment, \
                and change of control provisions, with per-clause F1 scores ranging from 89.2% to 99.1%. \
                A key innovation is our hierarchical attention mechanism that processes documents up to 128,000 tokens by first identifying relevant sections \
                through a lightweight classifier before applying detailed clause extraction, reducing inference time by 75% compared to processing the full document. \
                We introduce a confidence calibration layer that provides well-calibrated probability estimates for each extracted clause, \
                enabling automated triage where high-confidence extractions are accepted and low-confidence ones are routed to human reviewers. \
                Deployment at three Am Law 100 firms demonstrates 60% reduction in contract review time with zero missed critical clauses \
                over a 6-month evaluation period covering 12,000 contracts.""",
                    r1, nlp, now.minusMonths(4), true);

            pub(pubRepo, "Drug-Target Interaction Prediction using Bilinear Attention Networks", """
                We present DrugBANet, a bilinear attention model for predicting novel drug-protein binding affinities, \
                validated experimentally on three independent assay panels covering kinase inhibitors, GPCR modulators, and protease targets. \
                The model represents drugs as molecular graphs processed by a graph isomorphism network (GIN) \
                and proteins as sequences processed by a pre-trained ESM-2 protein language model, \
                combining both representations through a bilinear attention mechanism that learns interaction-specific cross-modal features. \
                On the BindingDB benchmark, DrugBANet achieves an AUROC of 0.943 and an AUPRC of 0.891 for binary binding classification, \
                and a Pearson correlation of 0.847 for binding affinity regression (pKd prediction). \
                We validate the model's predictions by synthesizing and testing 15 computationally identified kinase inhibitor candidates, \
                of which 11 show sub-micromolar activity in biochemical assays, corresponding to a 73% experimental hit rate. \
                The attention maps reveal interpretable binding site interactions that align with known pharmacophore models, \
                providing insights for medicinal chemistry optimization. Our virtual screening pipeline processes 10 million compounds \
                against a target protein in under 4 hours on a single GPU, enabling rapid hit identification for drug discovery campaigns. \
                We release our trained model and a web server for interactive drug-target interaction prediction.""",
                    r8, bioinfo, now.minusMonths(4), false);

            pub(pubRepo, "Contrastive Pre-training for Source Code Understanding and Bug Detection", """
                We present CodeContrast, a self-supervised model that learns semantic representations of source code from 2 million GitHub repositories, \
                improving downstream bug detection by 23% over supervised baselines and code clone detection by 18% over CodeBERT. \
                Our contrastive learning objective creates positive pairs from semantically equivalent code transformations \
                (variable renaming, loop refactoring, method extraction) and negative pairs from functionality-altering mutations, \
                teaching the model to distinguish syntactic variation from semantic change. \
                The model processes code as a hybrid of token sequences and abstract syntax trees (ASTs), \
                using a structure-aware Transformer that attends to both sequential and hierarchical relationships. \
                On the Devign vulnerability detection benchmark, CodeContrast achieves 65.3% F1 compared to 58.4% for CodeBERT and 53.1% for a fine-tuned GPT-3.5. \
                For code clone detection on BigCloneBench, we report 96.8% F1 on Type-3 (syntactically different but functionally similar) clones, \
                the most challenging category. We also demonstrate strong performance on code summarization (26.4 BLEU on CodeSearchNet) \
                and code search (MRR of 0.782 on the same benchmark). The pre-trained model generalizes across 6 programming languages \
                (Python, Java, JavaScript, Go, C, and C++) without language-specific fine-tuning.""",
                    r7, nlp, now.minusMonths(3), false);

            pub(pubRepo, "Generative Design of Soft Robotic Actuators via Diffusion Models", """
                We present DiffuSoft, a topology optimization framework that uses denoising diffusion probabilistic models \
                to design pneumatic soft robotic actuators with 3x improved force-to-weight ratio compared to human-designed baselines. \
                The diffusion model is conditioned on desired actuator specifications (force output, range of motion, response time, and material constraints) \
                and generates 3D printable geometries that satisfy these requirements while optimizing internal channel topology for efficient pneumatic actuation. \
                Training data consists of 100,000 simulated actuator designs evaluated using a high-fidelity finite element model (Abaqus) \
                for mechanical performance and computational fluid dynamics for pneumatic response time. \
                The generated designs exhibit novel internal channel geometries including fractal branching patterns and helical arrangements \
                that were never present in the training data, demonstrating the model's ability to discover genuinely novel engineering solutions. \
                We fabricate 20 generated designs using multi-material 3D printing (Stratasys J750) and validate them experimentally, \
                confirming that simulated performance predictions match real-world measurements within 8% error. \
                The best generated actuator produces 12.5 N of force at 150 kPa input pressure while weighing only 23 grams, \
                compared to 4.2 N for a conventional bellows actuator of similar size. \
                Applications include minimally invasive surgical instruments, adaptive prosthetic hands, and compliant grippers for delicate object manipulation.""",
                    r3, robotics, now.minusMonths(3), false);

            pub(pubRepo, "Bias Mitigation in Large Language Models via Constitutional AI and RLHF", """
                We present a comprehensive methodology for reducing toxic, biased, and harmful outputs from large language models \
                using a combination of Reinforcement Learning from Human Feedback (RLHF) and Constitutional AI principles. \
                Our approach trains a reward model on 50,000 human preference comparisons covering toxicity, bias, factuality, and helpfulness, \
                then uses Proximal Policy Optimization (PPO) to align the language model with these preferences. \
                The constitutional component adds a self-critique step where the model evaluates its own outputs against a set of 15 ethical principles \
                before generating a final response, reducing the need for expensive human feedback by 70%. \
                On the RealToxicityPrompts benchmark, our method reduces toxicity probability from 0.29 to 0.04 (86% reduction) \
                while maintaining task performance within 2% on MMLU, HellaSwag, and TruthfulQA benchmarks. \
                Bias evaluation using the BBQ (Bias Benchmark for QA) shows a 67% reduction in stereotypical responses across 9 demographic categories \
                including race, gender, religion, disability, and socioeconomic status. \
                We also address the challenge of over-refusal, where overly cautious models refuse legitimate requests, \
                by introducing a nuanced reward signal that distinguishes between genuinely harmful queries and benign questions about sensitive topics. \
                Our trained model is released with a detailed model card documenting known limitations, evaluation results, and deployment guidelines.""",
                    r4, ethics, now.minusMonths(2), true);

            pub(pubRepo, "Diffusion Models for Privacy-Preserving Synthetic Medical Image Generation", """
                We develop a conditional diffusion model for generating synthetic CT scan images that preserve diagnostic utility \
                while providing formal differential privacy guarantees for patient data protection. \
                Our model, DP-MedDiffusion, is trained with differentially private stochastic gradient descent (DP-SGD) \
                at a privacy budget of epsilon=8, producing synthetic images that pass radiologist Turing tests with 62% accuracy \
                (where 50% represents indistinguishable from real images). \
                The generated images achieve a Frechet Inception Distance (FID) of 12.3 on lung CT scans and 15.7 on brain MRI, \
                representing state-of-the-art quality for privacy-preserving medical image synthesis. \
                We validate diagnostic utility by training downstream classification models on synthetic data alone: \
                a lung nodule detector achieves 91.2% sensitivity (compared to 94.8% when trained on real data), \
                and a brain tumor classifier achieves 88.7% accuracy (compared to 93.1% on real data). \
                The key innovation is a pathology-aware conditioning mechanism that ensures generated images contain realistic and diverse pathological findings \
                rather than only generating healthy anatomy, which is a common failure mode of unconditional medical image generators. \
                Our model enables multi-institutional medical AI research by allowing hospitals to share synthetic datasets \
                instead of real patient images, facilitating compliance with HIPAA, GDPR, and other healthcare privacy regulations. \
                We release a curated synthetic dataset of 50,000 CT scans for public research use.""",
                    r10, emerging, now.minusMonths(2), false);

            pub(pubRepo, "Zero-Shot Cross-Modal Generalization in Vision-Language Models", """
                We investigate prompt engineering strategies and architectural modifications enabling CLIP-based vision-language models \
                to recognize novel object categories, attribute compositions, and spatial relationships without any fine-tuning on the target domain. \
                Our method, CompositionalCLIP, decomposes complex visual concepts into primitive attributes (shape, color, texture, spatial relation) \
                and constructs compositional text prompts that combine these primitives, achieving 34.2% accuracy on the MIT-States compositional zero-shot benchmark \
                compared to 18.7% for vanilla CLIP and 28.9% for CoOp. \
                We introduce a visual concept algebra that performs arithmetic operations in the CLIP embedding space, \
                enabling relational reasoning such as 'image of A next to B' = embed(A) + spatial_offset + embed(B). \
                The approach generalizes to visual question answering without VQA-specific training, \
                achieving 52.1% accuracy on VQAv2 zero-shot (compared to 48.3% for Flamingo-3B and 56.8% for the fully fine-tuned BLIP-2). \
                We systematically evaluate failure modes including attribute binding errors, spatial relation confusion, \
                and counting failures, providing diagnostic tools for the community. \
                Our analysis shows that CLIP's zero-shot performance is strongly correlated with the frequency of concept co-occurrence in the pre-training data, \
                suggesting that true compositional generalization remains an open challenge for current vision-language architectures.""",
                    r9, cv, now.minusMonths(1), true);

            pub(pubRepo, "Quantum Error Correction with Rotated Surface Codes", """
                We present an implementation of rotated surface codes on a 2D superconducting qubit lattice \
                achieving logical error rates below the fault-tolerance threshold of 1% per syndrome extraction cycle. \
                Our 17-qubit surface code prototype demonstrates a logical Z error rate of 0.3% per round, \
                representing a 3.2x improvement in logical error suppression compared to the physical qubit error rate of 0.9%. \
                The key hardware innovation is a frequency-tunable coupler design that enables high-fidelity two-qubit CZ gates (99.5% fidelity) \
                while maintaining qubit coherence times exceeding 100 microseconds. \
                We introduce a real-time decoder based on a recurrent neural network trained on simulated syndrome data, \
                achieving decoding latency below 1 microsecond — fast enough for real-time error correction during computation. \
                The decoder maintains accuracy within 0.5% of the optimal minimum-weight perfect matching decoder while being 100x faster. \
                We demonstrate that our surface code suppresses logical errors exponentially with code distance, \
                extrapolating to logical error rates below 10^-12 at code distance 21, which would enable practical quantum computation. \
                Memory lifetime experiments show that our encoded logical qubit maintains coherence 5.2x longer than any individual physical qubit, \
                providing the first unambiguous demonstration of quantum error correction extending the useful lifetime of quantum information \
                in a superconducting processor.""",
                    r5, quantum, now.minusMonths(1), false);

            pub(pubRepo, "Adversarial Robustness Evaluation of Vision-Language Safety Systems", """
                We present a systematic evaluation of adversarial attacks on multimodal vision-language models used for content moderation and safety filtering, \
                revealing critical vulnerabilities in systems deployed by major social media platforms. \
                Our attack framework generates adversarial images that bypass safety classifiers while maintaining human-perceivable harmful content, \
                achieving a 78% attack success rate against CLIP-based content filters and 65% against LLaVA-based safety systems. \
                We develop three novel attack strategies: typographic attacks that embed harmful text in decorative fonts unreadable by OCR, \
                semantic perturbation attacks that shift image embeddings away from harmful cluster centers while preserving visual content, \
                and multi-modal jailbreaks that combine benign text prompts with adversarial images to elicit harmful model outputs. \
                Our defense contributions include an ensemble adversarial training scheme that improves robustness by 43% \
                against unseen attack types while maintaining clean accuracy within 1.2% of the undefended model. \
                We also propose a certified defense based on randomized smoothing in the CLIP embedding space \
                that provides provable robustness guarantees against Lp-bounded perturbations. \
                Responsible disclosure: all vulnerabilities were reported to affected platform operators 90 days before publication, \
                and our attack code is released with a delayed publication schedule to allow patching. \
                We recommend that safety-critical deployments of vision-language models adopt adversarial evaluation as a standard testing procedure \
                and maintain human-in-the-loop review for edge cases identified by our detection framework.""",
                    r10, emerging, now, true);

            // ── News ──
            news(newsRepo, "Annual Research Symposium 2025", "Invitation ouverte aux chercheurs pour le symposium annuel de l'IA-Technology. Sessions sur le NLP, la vision et la robotique. Plus de 200 presentations prevues cette annee.", admin, true);
            news(newsRepo, "Nouveau partenariat avec le CNRS", "L'IA-Technology signe un accord de collaboration avec le Centre National de la Recherche Scientifique pour les 3 prochaines annees.", admin, true);
            news(newsRepo, "Publication record ce trimestre", "Notre plateforme a enregistre un nombre record de 850 publications au cours du dernier trimestre.", mod, false);
            news(newsRepo, "Lancement du programme doctoral", "Un nouveau programme doctoral interdisciplinaire en IA est ouvert aux candidatures pour la rentree 2025.", admin, true);
            news(newsRepo, "Mise a jour de la plateforme v2.0", "Nouvelles fonctionnalites : recherche semantique, profils chercheurs IA, et resumes automatiques.", mod, false);
            news(newsRepo, "Conference internationale ICML 2025", "Trois de nos chercheurs presenteront leurs travaux a la conference ICML cette annee.", admin, true);
            news(newsRepo, "Atelier ethique de l'IA", "Un atelier sur les biais algorithmiques et l'equite en IA sera organise le mois prochain.", franck, false);
            news(newsRepo, "Bourse de recherche disponible", "Nouvelle bourse de 15 000 euros pour les doctorants travaillant sur l'IA appliquee a la sante.", mod, false);
            news(newsRepo, "Hackathon IA-Technology 2025", "Participez au hackathon annuel dedie aux applications innovantes de l'intelligence artificielle.", franck, true);
            news(newsRepo, "Resultats du challenge NLP", "Les resultats du challenge de traitement automatique du langage sont disponibles.", admin, false);
        };
    }

    private User user(UserRepository repo, PasswordEncoder encoder, String name, String email, String pwd, Role role, boolean active) {
        var u = new User();
        u.setUsername(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(pwd));
        u.setRole(role);
        u.setActive(active);
        return repo.save(u);
    }

    private Domain domain(DomainRepository repo, String name, String desc, String category) {
        var d = new Domain();
        d.setName(name);
        d.setDescription(desc);
        d.setCategory(category);
        return repo.save(d);
    }

    private Researcher researcher(ResearcherRepository repo, String fn, String ln, String email, String bio, Domain d) {
        var r = new Researcher();
        r.setFirstName(fn);
        r.setLastName(ln);
        r.setEmail(email);
        r.setBio(bio);
        r.setDomain(d);
        return repo.save(r);
    }

    private void pub(PublicationRepository repo, String title, String abs, Researcher r, Domain d, LocalDateTime date, boolean featured) {
        var p = new Publication();
        p.setTitle(title);
        p.setAbstract_(abs.strip());
        p.setResearcher(r);
        p.setDomain(d);
        p.setPublishedDate(date);
        p.setFeatured(featured);
        repo.save(p);
    }

    private void news(NewsRepository repo, String title, String content, User author, boolean featured) {
        var n = new News();
        n.setTitle(title);
        n.setContent(content);
        n.setAuthor(author);
        n.setFeatured(featured);
        repo.save(n);
    }
}
